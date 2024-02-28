package jpabook.repository;

import jakarta.persistence.EntityManager;
import jpabook.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item);   // 완전히 새로 생성
        } else {
            em.merge(item);     // 이미 DB에 등록된 것을 가져옴. 이것은 update, 실무에서 쓸 일이 거의 없다.
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
