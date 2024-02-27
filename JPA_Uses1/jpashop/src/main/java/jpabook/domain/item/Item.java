package jpabook.domain.item;

import jakarta.persistence.*;
import jpabook.domain.Category;
import jpabook.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // 한 테이블에 다 상속된 엔티티객체가 모두 있다. 객체로써 나누어 다루는 것
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * 비즈니스 로직, 재고를 늘리고 줄임 - 도메인 주도 설계로 엔티티안에서 처리하는 것이 객체 지향적이고 응집도가 높다
     */
    /*
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
