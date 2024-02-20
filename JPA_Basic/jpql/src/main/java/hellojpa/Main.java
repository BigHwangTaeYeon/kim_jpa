package hellojpa;

import hellojpa.jpql.Member;
import hellojpa.jpql.MemberDTO;
import hellojpa.jpql.MemberType;
import hellojpa.jpql.Team;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // JPA 정석
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            em.persist(member2);

            em.flush();
            em.clear();

            String query = "select group_concat(m.username) from Member m ";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();

            for(String m : result) {
                System.out.println("Objects = " + m);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}