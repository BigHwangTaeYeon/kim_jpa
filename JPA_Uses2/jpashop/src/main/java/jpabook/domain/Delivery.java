package jpabook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery")    // mappedBy, 읽기 전용으로 전환
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)    // EnumType 디폴트는 ORDINAL로 되어있다. 꼭 String 으로 사용하자. 정확하게 파악할 수 있다.
    private DeliveryStatus status;
}
