# DTO 직접 조회
Repository package 정리
repository
    Order Entity 등(핵심 비즈니스)
query
    화면이나 API 의존관계

화면의 API 돌아가는 라이프사이클과 핵심 비즈니스 라이프사이클이 다르다.

### N+1 문제

```java
@GetMapping("/api/v4/orders")
public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
}
public class OrderQueryRepository {
    // 컬렉션을 바로 넣을 수 없다 그래서 DTO 의 생성자에tj orderItems 를 뺐다
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }
}
```
Query 루트 1번, 컬렉션 N 번 실행
1. findOrders 조회
2. findOrderItems 조회 (데이터가 두 개이기에 두번 조회)
결국에 N+1 이다.

### N+1 문제 성능 최적화
order_item 을 DB 에서 다시 조회해서 가져오는 것이 아닌,
조회한 order 데이터의 id 들을 list 로 담아 IN 으로 조회 함으로써, 한번에 orderItem 을 가져온다.

그리고 map 으로 변환하여 OrderItems 에서 찾아서 담아준다.(이게 핵심)
```java
public List<OrderQueryDto> findAllByDto_optimization() {
    List<OrderQueryDto> result = findOrders();

    List<Long> orderIds = result.stream()
            .map(OrderQueryDto::getOrderId)
            .collect(Collectors.toList());

    List<OrderItemQueryDto> orderItems = em.createQuery(
                    "select new jpabook.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                            "from OrderItem oi " +
                            "join oi.item i " +
                            "where oi.order.id in :orderIds ", OrderItemQueryDto.class
            )
            .setParameter("orderIds", orderIds)
            .getResultList();

    // OrderId 기준으로 Map 으로 변환
    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
            .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

    // 처음과는 달리 DB 에서 가져오는 것이 아닌 메모리에 담긴 데이터를 가져온다
    result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
}
```

리팩토링
```java
public List<OrderQueryDto> findAllByDto_optimization() {
    List<OrderQueryDto> result = findOrders();

    Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

    // 처음과는 달리 DB 에서 가져오는 것이 아닌 메모리에 담긴 데이터를 가져온다
    result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
}
```

### 쿼리 한방으로 최적화
createQuery 에 모두 join 을 하면 중복이 포함되고 페이징을 할 수 있지만 중복되기에 원하는 페이징이 아니다.

한번의 쿼리로 모든 데이터를 가져와서 중복된 데이터를 정리하기 위해,
생성자를 통해 각 dto 객체를 생성하고 다시 클라이언트에게 보내줄 dto 에 모두 담아 반환한다.
```java
@GetMapping("/api/v6/orders")
public List<OrderQueryDto> ordersV6() {
    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

    // collect groupBy 할 때, 어떤 것을 기준으로 묶어야 할지 모르기 때문에 정규화가 되지 않는다.
    // OrderQueryDto 클레스에 @EqualsAndHashCode(of = "orderId") 해줌으로 orderId 기준으로 묶어주는 기능을 하게 되어 완벽한 데이터가 형성된다.
    return flats.stream()
            .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                    mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
            )).entrySet().stream()
            .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
            .collect(toList());
}
```
장점
    쿼리 한번
단점
    쿼리는 한번이지만 조인으로 인해 중복 데이터가 추가되어 상황상 더 느려질 수 있다.
    어플리케이션에서 추가 작업이 크다
    페이징 불가능

v5 가 낫다.