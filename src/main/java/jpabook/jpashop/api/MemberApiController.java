package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest createMemberRequest) {
        Member member = new Member();
        member.setName(createMemberRequest.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 원래 PUT은 전체 업데이트를 할때 사용하는것임 부분 업데이트를 하려면 PATCH를 사용해야 rest 스타일임
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable(name = "id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findById(id).orElseThrow();
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> findMembersV1() {
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        //엔티티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);

    }


    @Data
    static class Result<T> {
        private T data;

        public Result(T data) {
            this.data = data;
        }
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }


    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;

        public CreateMemberRequest(String name) {
            this.name = name;
        }
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


}
