package jpabook.api;

import jpabook.domain.Address;
import jpabook.domain.Order;
import jpabook.domain.OrderItem;
import jpabook.domain.OrderStatus;
import jpabook.repository.OrderRepository;
import jpabook.repository.OrderSearch;
import jpabook.repository.order.query.OrderFlatDto;
import jpabook.repository.order.query.OrderItemQueryDto;
import jpabook.repository.order.query.OrderQueryDto;
import jpabook.repository.order.query.OrderQueryRepository;
import jpabook.service.OrderQueryService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderQueryService orderQueryService;

    /*
     * 컬렉션 노출
     */
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

    /*
     * DTO 로 노출, 자그마치 11번 조회
     */
    @GetMapping("/api/v2/orders")
    public List<OrderQueryService.OrderQueryDto> ordersV2() {
//        List<Order> all = orderRepository.findAllString(new OrderSearch());
//        List<OrderDto> collect = all.stream()
//                .map(o -> new OrderDto(o))
//                .collect(toList());
        return orderQueryService.collectV2();
    }

    /*
     * DTO 로 노출, 자그마치 11번 조회
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithItem();

        List<OrderDto> collect = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /*
     * 컬렉션 페치 조인, 페이징 처리
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset
            , @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> collect = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /*
     * DTO 직접 조회
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /*
     * DTO 직접 조회, N+1 성능 최적화
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /*
     * 쿼리 한방으로 성능 최적화
     */
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

    // @Data 쓰면 toString 등 다만들기때문에 조심 !
    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
//        private List<OrderItem> orderItems;
//      OrderDto 의 OrderItem 을 그대로 반환하면 안된다.
//      완전히 엔티티와의 의존관계를 끊어야 한다.
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            this.orderId = o.getId();
            this.name = o.getMember().getName();
            this.orderDate = o.getOrderDate();
            this.orderStatus = o.getStatus();
            this.address = o.getDelivery().getAddress();
            // 초기화
//            o.getOrderItems().stream().forEach(order->order.getItem().getName());
//            this.orderItems = o.getOrderItems();
            this.orderItems = o.getOrderItems().stream()
                    .map(orderItem->new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;    // 상품명
        private int orderPrice;     // 주문가격
        private int count;          // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
