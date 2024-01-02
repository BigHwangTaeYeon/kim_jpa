# JPA

Persistence 생성부터 트렌젝션으로 Member 객체에 insert 하는 구문
EntityManger 부분과 트렌젝션 경계 등 Spring 이 다 제공해준다.
```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();
tx.begin();
// code
try {
    // Insert
    Member member = new Member();
    member.setId(2L);
    member.setName("HelloB");

    em.persist(member); // 저장 개념

    tx.commit();
} catch (Exception e) {
    tx.rollback();
} finally {
    em.close();
}
emf.close();

// Delete
Member findMember = em.find(Member.class, 1L);
em.remove(findMember);

// Select
Member findMember = em.find(Member.class, 1L);
System.out.println("findMember.id = " + findMember.getId());
System.out.println("findMember.name = " + findMember.getName());

// Update
Member findMember = em.find(Member.class, 1L);
findMember.setName("HelloJPA");
//em.persist(member);
tx.commit();
// EntityManager에 persist로 저장하지 않아도 Update가 된다.
// JPA통해 Entity를 가져오면 JPA에서 관리를 한다.
// Commit하는 시점에서 변경이 됬는지 어떻게 됬는지 확인을 하기에 자동으로 Update Query가 실행된다.
```

EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
EntityManager em = emf.createEntityManager();

1. 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유한다.
2. 엔티티 매니저는 쓰레드간에 공유하면 안된다.(사용하고 버려야 한다)
3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행한다.

### JPQL 소개
    가장 단순한 조회 방법
        - EntityManager.find()
        - 객체 그래프 탐색(a.getB().getC())
    나이가 18살 이상인 회원을 모두 검색
```java
List<Member> result = em.createQuery("select m from Member as m", Member.class)
        .setFirstResult(5)  // 5번부터 
        .setMaxResults(8)   // 8개 가져와, Pagingnation
        .getResultList();

for(Member member : result){
    System.out.println("member.name = " + member.getName());
}
```

JPQL ( em.createQuery("select m from Member as m", Member.class) )
JPA 를 사용하면 엔티티 객체를 중심으로 개발
문제는 검색 쿼리
검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요