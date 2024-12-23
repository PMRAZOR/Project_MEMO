package com.memo.members.controllers;

import com.memo.entity.Member;
import com.memo.members.dto.JoinRequest;
import com.memo.members.dto.LoginRequest;
import com.memo.members.dto.ProfileUpdateRequest;
import com.memo.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    //회원가입
    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinRequest request) {
        return memberService.join(request);
    }

    //로그인
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        return memberService.login(request);
    }

    @GetMapping("/{id}")
    public Member getUserInfo(@PathVariable int id){
        return memberService.findById(id);
    }

    // 프로필 수정
    @PatchMapping(value = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProfile(
            @PathVariable int id,
            @ModelAttribute ProfileUpdateRequest request) {

        memberService.updateProfile(id, request);
    }
    // 회원 삭제
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable int id) {
        memberService.deleteMember(id);
    }
}
