package jpabook.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    // 컬렉션을 바로 넣을 수 없다 그래서 DTO 의 생성자에tj orderItems 를 뺐다
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // 처음과는 달리 DB 에서 가져오는 것이 아닌 메모리에 담긴 데이터를 가져온다
        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
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
        return orderItemMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId ", OrderItemQueryDto.class
        )
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                "from Order o " +
                                "join o.member m " +
                                "join o.delivery d ", OrderQueryDto.class
                )
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d " +
                        "join o.orderItems oi " +
                        "join oi.item i ", OrderFlatDto.class
        )
                .getResultList();
    }
}
