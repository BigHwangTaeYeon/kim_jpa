```java
public void merge() throws Exception {
    Book book = em.find(Book.class, 1L);
    book.setName("sadf");
    // TX commit 
}
```
트렌젝션 커밋이 되면 JPA 가 변경분에 대해 찾아서 Update 쿼리를 찾아서 데이터베이스에 반영한다.
이것이 터티체킹(dirty checking)이다. 이것이 변경감지.

### 준영속 엔티티
영속성 컨텍스트가 더는 관리하지 않는 엔티티를 말한다.

```java
@PostMapping("items/{itemId}/edit")
public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){

    Book book = new Book();
    book.setId(form.getId());
    book.setName(form.getName());
    book.setPrice(form.getPrice());
    book.setStockQuantity(form.getStockQuantity());
    book.setAuthor(form.getAuthor());
    book.setIsbn(form.getIsbn());

    itemService.saveItem(book);
    return "redirect:/items";
}
```
html 에서 submit 하면 BookForm 을 통해 데이터가 전달된다.
위에 보면 Id 값을 BookForm 에서 가져와 book 객체에 넣어준다.
이것은 한번 조회를 하여 가져온 결과라는 것이다.
기존의 식별자가 정확하게 있으면 이런것을 준영속 상태라고 한다.(실제 데이터 베이스에 갔다온 친구)

준영속 상태는 JPA 가 관리하지 않아 변경감지가 되지 않는다.
그래서 수정하는 방법이 따로 있다.
1. 변경 감지 기능 사용  (이게 좋은 방법)
    ```java
    @Transactional
    public void updateItem(Long itemId, Book bookParam) {
        Item findItem = itemRepository.findOne(itemId); // findItem 은 영속 상태
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());    // 이렇게 실행되면 flush() 가 실행되고 commit 이 일어난다.
    }
    ```

2. 병합(merge) 사용   (실무에서 잘 사용하지 않는다.)
    ```java
    @Repository
    @RequiredArgsConstructor
    public class ItemRepository {
        private final EntityManager em;
    
        public void save(Item item) {
            if (item.getId() == null) {
                em.persist(item);   // 완전히 새로 생성
            } else {
                em.merge(item);     // 이미 DB에 등록된 것을 가져옴. 이것은 update, 실무에서 쓸 일이 거의 없다.
            }
        }
    }
    ```
merge()가 1번 처럼 findOne()으로 데이터 찾고 다시 set으로 전부 담아서 update 처리를 해준다.
1) merge() 실행
2) 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티 조회
    2-1) 만약 1차 캐시에 엔티티가 없으면 DB에서 엔티티 조회하고, 1차 캐시에 저장
3) 조회한 영속 엔티티(mergeMember)에 member 엔티티의 값을 채워 넣는다.
    (member 엔티티의 모든 필드 값을 mergeMember 에 밀어 넣는다. 이때 mergeMember 의 "회원1"이라는 이름이 "회원명변경"으로 바뀐다.)
4) 영속 상태인 mergeMember 를 반환 (아래 코드와 같은 형태)

    ```java
    @Transactional
    public Item updateItem(Long itemId, Book bookParam) {
        Item findItem = itemRepository.findOne(itemId); // findItem 은 영속 상태
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());    // 이렇게 실행되면 flush() 가 실행되고 commit 이 일어난다.
        return findItem;
    }
    ```
반환된 Item merge = em.merge(item);의 item 매개변수는 준영속 상태이며 변수 merge 는 JPA 가 관리하고 있는 영속 상태이다.

주의 : 변경 감지 기능을 사용하면 원하는 속성만 선택하여 변경이 가능하지만,
    병합을 사용하면 모든 속성이 변경된다.
병합 시 값이 없으면 null 로 업데이트 할 위험도 있다.(병합은 모든 필드를 교체한다.)
(만약 price 를 변경하지 않고 기존대로 간다면 price 값을 넘기지 않아 null 값이 넘어가 update 가 null 값으로 된다.)
