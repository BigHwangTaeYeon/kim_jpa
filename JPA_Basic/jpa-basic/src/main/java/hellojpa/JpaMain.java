package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.*;

public class JpaMain {
    public static void main(String[] args){
        // JPA 정석
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {

            Member member = new Member();
            member.setUserName("hello");
            member.setHomeAdress(new Adress("서울", "도산대로","285"));
            member.setWorkPeriod(new Period());

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

}
