package hellojpa.base;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    @ManyToMany(mappedBy = "items")
    private List<Category> categorys = new ArrayList<>();
}
