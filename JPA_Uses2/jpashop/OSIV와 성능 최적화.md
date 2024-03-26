# OSIV와 성능 최적화

Open Session In View : 하이버네이트<br>
Open EntityManager In View : JPA<br>
(관례상 OSIV라 한다.)

모르면 장애로 이어질 수 있다.

spring boot 를 실행하면 warn 이 뜬다<br>
2024-03-26T17:14:56.623+09:00  WARN 10480 --- [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : <br>
spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. <br>
Explicitly configure spring.jpa.open-in-view to disable this warning

언제 데이터베이스 커넥션을 가지고 오느냐, 이 문제이다.

OSIV 전략은 트랙잭션 시작처럼 최초 데이터베이스 커넥션 시작 시점부터 API 응답이 끝날 때 까지 영속성 컨텍스트와 데이터베이스 커넥션을 유지한다.<br>
(데이터베이스 트랜잭션이 시작할 때, JPA 영속성 컨텍스트라는 애가 데이터베이스 커넥션을 가져온다.)<br>
(영속성 컨텍스트는 컨트롤러에서 유저에게 반환 할 때까지 살아있다. 그래서 레이지 로딩까지 컨트롤이 가능 했던 것이다.)<br>
그래서 지금까지 View Template 나 API 컨트롤러에서 지연로딩이 가능했던 것이다.

지연 로딩은 영속성 컨텍스트가 살아있어야 가능하고,<br>
영속성 컨텍스트는 기본적으로 데이터베이스 커넥션을 유지해야한다.<br>
이것 자체가 큰 장점이다

하지만 트레이드 오프가 있다.<br>
너무 오랜시간 동안 데이터베이스 커넥션 리소스를 사용하기 때문에,<br>
실시간 트래픽이 중요한 애플리케이션에서는 커넥션이 모자랄 수 있다.<br>
이것은 결국 장애로 이어진다.<br>
(API 가 3 초 걸리면 그때 동안 반환지 못해서 유지하게 된다.)

### OSIV OFF
영속성 컨텍스트 범위(트랜잭션 범위, @Transactional)에서만 유지된다.<br>
따라서 "커넥션 리소스를 낭비하지 않는다."

OSIV 를 끄면 모든 지연 로딩을 트랜잭션 안에서 처리해야 한다.<br>
따라서 지금까지 작성한 많은 지연 로딩 코드를 트랜잭션 안으로 넣어야 한다는 단점이 있다.<br>
그리고 view template 에서 지연 로딩이 동작하지 않는다.

결론적으로 트랜잭션이 끝나기 전에 지연 로딩을 강제로 호출해 두어야 한다.

```yaml
spring:
  jpa:
    open-in-view: false
```

org.hibernate.LazyInitializationException: could not initialize proxy [jpabook.domain.Member#1] - no Session
```java
@GetMapping("/api/v1/orders")
public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAllString(new OrderSearch());
    for (Order order : all) {
        // 객체 그래프 초기화
        // hibernate5Module 기본 설정 자체가 레이지 로딩했을 때, 프록시는 데이터를 안뿌리는데 초기화를하면 뿌리게 된다.
        order.getMember().getName();
        order.getDelivery().getAddress();
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.stream().forEach(o->o.getItem().getName());
    }
    return all;
}
```
order.getMember().getName();<br>
위 코드에서 Member 가 Proxy 인데 초기화를 못하게 되는 상황

OSIV 를 OFF 했기 때문에 Controller 에서 영속성 컨텍스트가 없는 상황,<br>
그러므로 초기화를 할 수 없다.<br>
그래서 트랜잭션 안에서 처리해주어야 한다.

##### LazyInitializationException 해결 방법

1. OrderQueryService 를 만들어서 쿼리용 Service 를 만든다.(영환님께서 선호하시는 방법)<br>
서비스 계층에서 트랜잭션 어노테이션을 사용하여 별도의 서비스를 제공한다.

```java
@GetMapping("/api/v2/orders")
public List<OrderDto> ordersV2() {
    List<Order> all = orderRepository.findAllString(new OrderSearch());
    List<OrderDto> collect = all.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());
    return collect;
}
```
에서

```java
@GetMapping("/api/v2/orders")
public List<OrderQueryService.OrderQueryDto> ordersV2() {
    return orderQueryService.collectV2();
}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public List<OrderQueryDto> collectV2() {
//    public List<OrderQueryDto> collectV2(List<Order> all) {
        List<Order> all = orderRepository.findAllString(new OrderSearch());
        return all.stream()
                .map(o -> new OrderQueryDto(o))
                .collect(toList());
    }
}
```
로 바꿔준다.

대부분 화면용 기능은 자주 바뀌기 때문에 라이프 사이클이 빠르다.<br>
핵심 비즈니스 로직이나 정책 서비스는 잘 바뀌지 않는다.<br>
둘 사이의 라이프 사이클은 다르다.

그래서 크고 복잡한 애플리케이션을 개발한다면,<br>
이 둘의 관심사를 명확하게 분리하는 선택은 유지보수 관점에서 충분히 의미 있다.



OSIV 를 ON 하면<br>
크게 신경쓰지 않고 Controller 같은 곳에서 구현하면 된다는 것이지만

성능을 생각하면 OFF 가 맞다.

고객 서비스의 실시간 API 는 OSIV 를 끄고,
ADMIN 처럼 커넥션을 많이 사용하지 않는 곳에서는 OSIV 를 키는 것이 낫다.

