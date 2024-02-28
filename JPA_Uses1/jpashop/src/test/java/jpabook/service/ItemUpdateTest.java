package jpabook.service;

import jakarta.persistence.EntityManager;
import jpabook.domain.item.Book;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {
    @Autowired
    EntityManager em;
    @Test
    @DisplayName("")
    public void merge() throws Exception {
        Book book = em.find(Book.class, 1L);
        //TX
        book.setName("sadf");
        // TX commit 되면
        // JPA가 변경분에 대해 찾아서 Update 쿼리를 찾아서 데이터베이스에 반영한다.
        // 이것이 터티체킹(dirty checking)이다. 이것이 변경감지.


    }
}
