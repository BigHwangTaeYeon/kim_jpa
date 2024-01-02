package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args){
        // JPA 정석
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            /*Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");*/
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("HelloJPA");
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)  // 5번부터
                    .setMaxResults(8)   // 8개 가져와, Pagingnation
                    .getResultList();

            for(Member member : result){
                System.out.println("member.name = " + member.getName());
            }

//            em.persist(member);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
