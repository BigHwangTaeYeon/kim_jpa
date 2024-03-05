package jpabook.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.domain.Member;
import jpabook.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController // @ResponseBody 데이터 자체를 json 이나 xml 로 보내자 하는 어노테이션이 포함되어있다.
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // 엔티티를 외부에 노출하지 말자 !

    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /*
     @Valid validation 관련된 것은 관리된다. (Entity 안에서 변수 위에 어노테이션을 넣어줄 수 있다. ex.@NotEmpty)
     @RequestBody json 으로 온 바디를 멤버로 바로 바꿔준다.
     *** 엔티티를 그대로 사용하지말고 별도의 dto 를 만들어 사용해야한다 ***
    */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * v1 보다 안전한 방법
     * CreateMemberRequest 만듦으로 Member 엔티티의 변수이름을 바꾸면 컴파일 오류가 난다. (더욱 안전하다)
     * 엔티티만 보고는 id name address 모두 넘어오는지 name 만 넘어오는지 모른다. CreateMemberRequest 보면 명확하다.
     * 그리고 CreateMemberRequest 안에서 @NotEmpty 확인하기 좋다
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * PUT은 멱등하다. 같은 것을 여러번 호출해도 결과는 똑같다.
     * CreateMemberResponse 와 UpdateMemberResponse 를 나누는 이유는 등록이랑 수정은 api 스펙이 다르다. 수정이 제한적이다.
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        // 수정할 때는 가급적이면 변경감지가 좋다.
        memberService.update(id, request.getName());
        // 변경성 메서드에 Member 를 그대로 반환하는 것은 좋지 않다. void 로 처리 아니면 id 정도만 반환
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor // dto 에는 롬복을 막쓰는 편이다. 데이터만 움직이고 로직이 있는 것도 아니기 때문에.
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }

}
