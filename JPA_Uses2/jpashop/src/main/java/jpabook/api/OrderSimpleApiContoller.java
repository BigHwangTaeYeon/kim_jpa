package jpabook.api;

import jpabook.domain.Order;
import jpabook.repository.OrderRepository;
import jpabook.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
