package jpabook.api;

import jpabook.domain.Address;
import jpabook.domain.Order;
import jpabook.domain.OrderStatus;
import jpabook.repository.OrderRepository;
import jpabook.repository.OrderSearch;
import jpabook.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.repository.order.simplequery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiContoller {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllString(new OrderSearch());
        // 객체를 json으로 만드는 잭슨 라이브러리 입장에서는
        // Order가 Member에 가고, Member에서는 Orders가 있다고 판단한다.

        for(Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress();
        }

        return all;
        //  그래서 무한루프에 걸려 데이터가 계속 생성이 된다.
    }

    /*
     * 최적화, DTO 로 반환
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // 여기서 N+1 문제, N 은 회원, 배송이 속해있다. 총 주문수는 2개이기에 하나씩 조회해서 총 5번 조회를 시도한다.
        List<Order> orders = orderRepository.findAllString(new OrderSearch());
        List<SimpleOrderDto> all = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return all;
    }

    /*
     * fetch join 으로 N+1 문제 해결
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        // 여기서 N+1 문제, N 은 회원, 배송이 속해있다. 총 주문수는 2개이기에 하나씩 조회해서 총 5번 조회를 시도한다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> all = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return all;
    }

    /*
     * Dto 수정, fetch join 과 동일해 보일 수 있지만 select 절에서 가져오는 것이 원하는 것만 가져오기 때문에 fetch join 은 network 에서 더 많이 사용된다.
     * 성능 최적화에 조금 더 낫지만(거의 차이 없다, 보통 from 절에서 성능 차이가 나기 때문. data 사이즈가 다르면 다를 수 있다.) 하지만 v4 는 재사용성이 불가능하다.
     * 오롯이 이 상황에서만 사용할 수 있는 DTO 이다.
     * 서로 장단점이 있다. 우열을 가릴 수 없는 상황.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        // 여기서 N+1 문제, N 은 회원, 배송이 속해있다. 총 주문수는 2개이기에 하나씩 조회해서 총 5번 조회를 시도한다.
        // OrderRepository 에서 분리하였다. 순수 Order 를 조회하는 것이 아니기 때문에.
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // 이때, LAZY 초기화 영속성 컨텍스트가 id 로 찾아오는데 없으면 DB 에서 찾는다
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // 이때, LAZY 초기화 영속성 컨텍스트가 id 로 찾아오는데 없으면 DB 에서 찾는다
            // 현재 order, member, delivery 조회하고 또 member, delivery 를 조회한다. LAZY 메커니즘을 이해해야한다.
            // 결과 주문수가 2개이기에 루프가 두번 돈다.
            // member_id delivery_id 를 보면 감이 온다.
            // 이것이 N + 1 문제
        }
    }
}
