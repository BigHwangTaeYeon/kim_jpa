package japbook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Embedded   // 생략가능
    private Address address;

    @OneToMany(mappedBy = "member") // Order 엔티티의 관계를 맺어주는(@ManyToOne) 변수 명을 넣어 줘야 한다.
    private List<Order> orders = new ArrayList<Order>();
    // member에 orders넣는게 좋은 설계는 아니다.
    // item에 orderItem을 안넣는 것처럼,
    // query를 order에서 시작하지 member에서 시작하지 않는다.

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
