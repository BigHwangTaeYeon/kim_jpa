package hellojpa;

import hellojpa.base.Category;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class category extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    @ManyToMany(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private List<Item> items = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
