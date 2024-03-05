package jpabook.service;

import jpabook.domain.item.Book;
import jpabook.domain.item.Item;
import jpabook.repository.ItemRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;

    @Test
    @DisplayName("")
    @Rollback(false)
    public void save() throws Exception {
        //given
        Item item = new Book();
        item.setName("kimBook");
        //when
        itemService.saveItem(item);
        item.setId(1L);
        item.setName("kimBook@");
        itemService.saveItem(item);
        //then
        assertEquals(item.getName(), itemRepository.findOne(item.getId()).getName());

        List<Item> all = itemRepository.findAll();
        for(Item i : all) {
           assertEquals(i.getName(), item.getName());
        }
    }
}