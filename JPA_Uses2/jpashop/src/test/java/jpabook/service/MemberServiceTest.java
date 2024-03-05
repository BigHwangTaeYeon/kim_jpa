package jpabook.service;

import jakarta.persistence.EntityManager;
import jpabook.domain.Member;
import jpabook.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("회원가입")
//    @Rollback(value = false)    // 디폴트가 test에서는 true기 때문에 insert가 안나간다.
    public void join() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");
        //when
        Long saveId = memberService.join(member);
        //then
//        em.flush(); // test에서 rollback 안하고 insert 하는 법
        assertEquals(member, memberRepository.findOne(saveId));
        // 같은 영속성 컨텍스트에 있으면 같다고 나온다.
    }
    @Test(expected = IllegalStateException.class)
    @DisplayName("중복회원가입")
    public void 중복회원가입() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
//        try {
//            memberService.join(member2);    // 예외 발생
//        } catch (IllegalStateException e) {
//            return; // 너무 지저분함
//        }           // 그래서 @Test(expected = IllegalStateException.class) 사용
        memberService.join(member2);
        //then
        fail("예외가 발생해야한다.");
    }
}