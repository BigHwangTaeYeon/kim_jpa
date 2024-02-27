package jpabook.service;

import jpabook.repository.MemberRepository;
import jpabook.domain.Member;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@AllArgsConstructor
@RequiredArgsConstructor // final 인 변수만 생성자로 만들어준다.
public class MemberService {

//    @Autowired  // field injection
    private final MemberRepository memberRepository;

//    @Autowired    // 요즘은 생성자가 하나만 있는 경우, 자동으로 Autowired 를 해준다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /*
     *  회원 가입
     */
    @Transactional
    public Long join(Member member) {
        // 중복 회원
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
