package hellojpa.jpql;

import javax.naming.Name;
import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;
    private int orderAmount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Team team;

    @ManyToOne

    @Embedded
    private Address address;

    public Long getId() {
        return id;
    }

    public int getOrderAmount() {
        return orderAmount;
    }
}
