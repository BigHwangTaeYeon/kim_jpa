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
            member.setUserName("J");
            em.persist(member);

            em.flush();
            em.clear();

            Member findMember2 = em.getReference(Member.class, member.getId());
            System.out.println("m2 : " + findMember2.getClass());

            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(findMember2));
            Hibernate.initialize(findMember2);
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(findMember2));


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
