1. 조회용 샘플 데이터 입력</br>
    장애 90%가 조회 데이터에서 나온다. (사람들이 많이 사용하니까.)
2. 지연 로딩과 조회 성능 최적화</br>
    N+1 문제를 겪는 상황(쿼리가 전부 나가는 상황으로 성능 문제)
3. 컬렉션 조회 최적화</br>
    1:1 또는 N:1 등 JOIN으로 데이터가 늘어나서 성능 최적화하기 어려움을 겪는 상황
4. 페이징과 한계 돌파</br>
    페이징 처리에서 회원이 하나고 오더가 두개면 데이터가 두배로 증가해버리는 상황
5. OSIV와 성능 최적화</br>
    Open Session In View - 사용 안하면 LAZY Exception 을 자주 만나게 되는 상황

### 지연로딩과 성능 최적화 XToOne(OneToOne, ManyToOne)

지연로딩으로 발생하는 성능 문제를 단계적으로 해결
****정말 중요한 부분 100% 이해 필요****

======== 아래 방법은 엔티티로 받았을 경우 문제에 대한 최적화 방법이다, 그러므로 이런게 있다라는 것만 알면 된다. ========

/*
* xToOne (ManyToOne, OneToOne) 관계 성능 최적화
* Order
* Order -> Member
* Order -> Delivery
  */
```java
@GetMapping("/api/v1/simple-orders")
public List<Order> orderV1() {
    List<Order> all = orderRepository.findAllString(new OrderSearch());
    // 객체를 json으로 만드는 잭슨 라이브러리 입장에서는
    // Order가 Member에 가고, Member에서는 Orders가 있다고 판단한다.
    return all;
    //  그래서 무한루프에 걸려 데이터가 계속 생성이 된다.
}
```
무한 루프가 걸린다.

Order Member Delivery OrderItem 엔티티에 양방향 연관관계가 설정되어있는데,
아래와 같이 서로의 연결 고리를 한쪽에서 @JsonIgnore 로 끊어주어야 한다.
```java
public class Delivery {
    @JsonIgnore
    @OneToOne(mappedBy = "delivery")    // mappedBy, 읽기 전용으로 전환
    private Order order;
}
public class OrderItem {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
public class Member {
    @JsonIgnore // 외부 노출에서 제외하고 싶다. (memberV1 에서 바디로 데이터를 받아보면 빠져있다.)
    @OneToMany(mappedBy = "member") // order table 에 있는 member 에 의해 매핑됬을 뿐이야 읽기 전용이 될 뿐이야
    private List<Order> orders = new ArrayList<>();
}
```
그래도 해결되지 않는다.
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0]->jpabook.domain.Order["member"]->jpabook.domain.Member$HibernateProxy$r6MisjWV["hibernateLazyInitializer"])
Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]\r\n\tat org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.writeInternal(AbstractJackson2HttpMessageConverter.java:489)\r\n\tat org.springframework.http.converter.

Order 를 가지고왔는데, Order 의 Member 가 지연로딩이므로 DB에서 가지져오지 않기 때문에 null 을 넣을 수는 없어
하이버네이트에서 new ProxyMember()를 생성해서 넣어둔다.
생성을 해주는 것이 bytebuddy 라이브러리에서 new ByteBuddyInterceptor()가 대신 들어가 있다.

프록시 객체를 가짜로 넣어놓고, Member 객체가 필요하다면 그때 DB 에서 가져온다고 생각하면 된다.(프록시 초기화)

이러한 과정을 거쳐야 하지만
@JsonIgnore 와 Jackson 라이브러리가 루프를 돌면서 Order 안에 Member 가 프록시로 문제가 생긴듯 하다.

하이버네이트 하이버 모듈을 설치해야한다.
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta'
@Bean
Hibernate5JakartaModule Hibernate5JakartaModule() {
    return new Hibernate5JakartaModule();
// 기본적으로 초기화된 프록시 객체만 노출된다.
// 초기화 되지 않은 프록시 객체는 노출하지 않는다.
}

```json
[
    {
        "id": 1,
        "member": null,
        "orderItems": null,
        "delivery": null,
        "orderDate": "2024-03-06T19:02:29.752497",
        "status": "ORDER",
        "totalPrice": 50000
    },
    {
        "id": 2,
        "member": null,
        "orderItems": null,
        "delivery": null,
        "orderDate": "2024-03-06T19:02:29.847242",
        "status": "ORDER",
        "totalPrice": 220000
    }
]
```
보면 member orderItems delivery 가 null 이다.
지연로딩같은 경우 아직 DB에서 조회한 것이 아니기에 JSON으로 뿌릴 때,
기본 전략이 module 에서는 지연로딩은 무시하라고 되어있다.
Hibernate5JakartaModule 을 이용해서 강제로 레이지 로딩도 가져오게 할 수 있다.
    이렇게 하면 필요 없는 데이터까지 조회하기에 좋은 방법이 아니다.
그리고 모듈 자체는 필요할 수 있지만 Lazy 때문에 모듈을 사용하는 것이 좋지않다

Module 로 지연로딩 설정을 지우고
```java
public class OrderSimpleApiContoller {
    private final OrderRepository orderRepository;
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }
}
```
이렇게 order 에서 Member Delivery 를 호출하면 DB에서 조회해온다.
(데이터를 사용하지 않는 것까지 다 노출하면 운영이 헬이 된다.)
그렇다고 LAZY 를 EAGER로 바꾸는 행위는 하지 말자.(다른 API가 문제가 된다.)
항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 페치 조인(fetch join)을 사용하자.

결국 DTO 로 변환하여 반환하는 것이 좋다.

***쿼리 방식 선택 권장 순서***
1. 엔티티를 DTO로 변환하는 방법
2. 필요하면 페치 조인으로 성능 최적화 -> 대부분 성능 이슈 해결
3. DTO로 직접 조회하는 방법
4. JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template로 SQL 직접 사용





### 컬렉션 조회 최적화 (OneToMany)
일대다 관계를 조회하고 최적화하는 방법

**fetch join 중복 제거**
@GetMapping("/api/v3/orders")를 조회하면 데이터가 4개가 나간다.
order 가 2개이고 orderItem 이 2개이기에 1:N 조건으로 총 4개의 데이터가 노출된다.
아래와 같이 order 의 중복을 제거하는 것이 필요하다

```java
public List<Order> findAllWithItem() {
    return em.createQuery("select distinct o from Order o " +
                            "join fetch o.member m " +
                            "join fetch o.delivery d " +
                            "join fetch o.orderItems oi " +
                            "join fetch oi.item i ", Order.class
    ).getResultList();
}
```
    select
        distinct o1_0.order_id,
        d1_0.delivery_id,
        d1_0.city,
        d1_0.street,
        d1_0.zipcode,
        d1_0.status,
    ...

distinct 를 사용하면 위와같이 select 구문이 나간다.
실제 SQL 에서는 distinct 를 사용하면 모든 컬럼이 같은 행만 중복 제거 되지만 
JPA 에서는 자체적으로 id 값이 같으면 중복 제거를 해준다.

********하지만 페이징이 불가능하다********
.setFirstResult(1)
.setMaxResults(100)
WARN HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory

쿼리를 보면 limit 또는 offset 이 없다.
결론은 모든 데이터를 가져온 후 메모리에서 페이징을 해버린다.(굉장히 위험하다.)

1:N 페치 조인에서는 페이징을 해서는 안된다.

컬렉션 페치 조인은 1개만 사용할 수 있다.
둘 이상의 페치조인을 사용하면 데이터가 부정합하게 조회될 수 있다.

###### 결론

1. 1:N 페치 조인에서는 페이징을 해서는 안된다.
2. 컬렉션 페치 조인은 1개만 사용할 수 있다.



### 페이징과 한계 돌파
* 컬렉션을 페치 조인하면 페이징이 불가능하다.
    order 를 기준으로 페이징 하려 하였지만 items 가 더 많기때문에 items 기준으로 데이터가 증가해서 orderItems 기준으로 페이징 되버린다.

1. ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. (ToOne 관계는 row 수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.)
2. 컬렉션은 지연 로딩으로 조회한다.
3. 지연 로딩 서능 최적화를 위해 hibernate.default_batch_fetch_size @BatchSize 를 적용한다.
   1) hibernate.default_batch_fetch_size : 글로벌 설정
   2) @BatchSize : 개별 최적화
   3) 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.

```java
public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d ", Order.class
        )
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
}
```
ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다.

```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```
where
    oi1_0.order_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ...
where
    i1_0.item_id in (?, ? ...
in query 의 개수를 정해준다.

중요한 것은 orderItem 이나 item 이 2개이기에 쿼리가 두번씩 실행됬지만,
in 을 사용함으로 한번의 쿼리를 사용하여 모두 가져온다.
("select o from Order o "만으로도 가능하지만 ToOne 관계의 member, delivery 쿼리를 실행하기 때문에 네트워크를 더 많이 타게 된다.)

기존 v3 에서는 한번의 쿼리로 모두 가져왔지만 중복 데이터가 많다.
v3.1에서는 정규화된 데이터로 중복이 없다.
정확하게 필요한 데이터만 정리해서 가져오게 된다.

데이터를 천개로 생각해본다면 네트워크를 호출하는 회수와 전송하는 사이에서 트레이드 오프가 있다/
적은 개수의 데이터라면 한방 쿼리가 낫지만 많으면 이것이 더 성능이 좋을 수 있다.
사실 상황에 많이 차이가 나게 된다.
영환님은 이 방법을 추천하는 편이다.

장점
    N+1 에서 1+1 로 최적화
    조인보다 DB 데이터 전송량이 최적화(정규화)
    호출 수는 증가해도 DB 데이터 전송량이 감소
    컬렉션 페치 조인은 페이징이 불가능 하지만 이 방법은 페이징이 가능하다.
결론
    ToOne 관계는 페치 조인하고 나머지는 batch size 로 최적화

이정도만 하더라도 충분한 성능 최적화가 된다
이 이상은 redis 를 사용하던지 다른 기술로 대체되는 정도이다.



##### Batch Size
yml 에서 글로벌 설정도 가능하지만 엔티티에 안에서 적용도 가능하다.

컬렉션이 아닌 것.
```java
//@BatchSize(size = 100)
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // 한 테이블에 다 상속된 엔티티객체가 모두 있다. 객체로써 나누어 다루는 것
@DiscriminatorColumn(name = "dtype")
public abstract class Item {}
```

컬렉션
```java
public class Order {
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}
```

미니멈은 없지만 멕시멈은 천개로 되어있다.
100 ~ 1000 사이로 권장
데이터베이스에 따라 제한을 두기도 하기에 확인이 필요

천개로 잡으면 순간적으로 DB에서 애플리케이션에 불러오기에 DB에 순간 부하가 증가할 수 있다.
하지만 100이든 1000이든 결국 전체 데이터를 로딩해야 하므로 메모리 사용량이 같다.

WAS DB 순간부하에 부담이 되면 100씩, 괜찮으면 1000 두어도 좋다.
