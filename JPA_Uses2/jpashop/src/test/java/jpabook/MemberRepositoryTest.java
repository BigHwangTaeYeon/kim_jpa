package jpabook;

import jpabook.repository.MemberRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

//    @Test
//    @Transactional  // test 에 transactional 에 있으면 실행 후, 롤백한다.
//    @DisplayName("")
//    @Rollback(false)    // test 의 롤백을 막는다.
//    public void testMember() throws Exception {
//        //given
//        Member member = new Member();
//        member.setUsername("memberA");
//        //when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//        //then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//        Assertions.assertThat(findMember).isEqualTo(member);
//        // 같은 영속성 컨텍스트에서 id 값(식별자)이 같으면 같다고 판단한다. 1차 캐시에서 데이터를 가져오기 때문
//        System.out.println("findMember == member : " + (findMember == member));
//    }

}