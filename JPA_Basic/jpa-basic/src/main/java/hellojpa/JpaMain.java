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

            Member member1 = em.find(Member.class, 150L);
            Member member2 = em.find(Member.class, 150L);
            member1.setName("AAAAA");

            em.detach(member1);

            System.out.println("=========================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
