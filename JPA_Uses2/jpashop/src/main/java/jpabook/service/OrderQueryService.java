package jpabook.service;


import jpabook.domain.Address;
import jpabook.domain.Order;
import jpabook.domain.OrderItem;
import jpabook.domain.OrderStatus;
import jpabook.repository.OrderRepository;
import jpabook.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    @Getter
    public static class OrderQueryDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //        private List<OrderItem> orderItems;
//      OrderDto 의 OrderItem 을 그대로 반환하면 안된다.
//      완전히 엔티티와의 의존관계를 끊어야 한다.
        private List<OrderItemQueryDto> orderItems;

        public OrderQueryDto(Order o) {
            this.orderId = o.getId();
            this.name = o.getMember().getName();
            this.orderDate = o.getOrderDate();
            this.orderStatus = o.getStatus();
            this.address = o.getDelivery().getAddress();
            // 초기화
//            o.getOrderItems().stream().forEach(order->order.getItem().getName());
//            this.orderItems = o.getOrderItems();
            this.orderItems = o.getOrderItems().stream()
                    .map(orderItem->new OrderItemQueryDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemQueryDto {
        private String itemName;    // 상품명
        private int orderPrice;     // 주문가격
        private int count;          // 주문수량

        public OrderItemQueryDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
