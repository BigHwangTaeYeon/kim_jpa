package hellojpa.base;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Category {
    @Id @GeneratedValue
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
          name = "category_item"
        , joinColumns = @JoinColumn(name = "category_id")
        , inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
    // 상위 카테고리
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    // 하위 카테고리
    @OneToMany(mappedBy = "parent")
    private List<Category> child;
}
