package japbook.jpashop.domain;

import javax.persistence.*;

@Entity
public class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_price")
    private int price;

    private int stockQuantity;
}
