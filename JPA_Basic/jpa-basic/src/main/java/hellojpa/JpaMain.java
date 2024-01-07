package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
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
            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");
//            member.changeTeam(team);
            em.persist(member);

//            em.flush();
//            em.clear();

//            team.getMembers().add(member);

            Team findTeam = em.find(Team.class, team.getId());
            List<Member> members = findTeam.getMembers();

            for(Member m : members){
                System.out.println("findTeam id : " + m.getName());
            }

//            Team newTeam = em.find(Team.class, 100L);
//            findMember.setTeam(newTeam);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
