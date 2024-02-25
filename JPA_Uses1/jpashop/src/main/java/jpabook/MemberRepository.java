package jpabook;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository // component 스켄 대상, 자동 Bean 등록
public class MemberRepository {

    @PersistenceContext // EntityManager 자동 주입
    private EntityManager em;

    // 커멘드랑 쿼리를 분리해라, 저장하고 나서 사이드 임펙트 커멘드 성이기에 리턴값을 안만들지만
    // 아이디 정도 있으면 조회가 가능하기에 id 리턴을 넣는다
    public Long save(Member member) {   // ctl shift T 테스트 클레스 생성
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
