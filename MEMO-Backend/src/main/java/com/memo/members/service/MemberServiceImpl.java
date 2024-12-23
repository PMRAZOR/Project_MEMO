package com.memo.members.service;

import com.memo.entity.Member;
import com.memo.file.constant.FileType;
import com.memo.file.dto.FileResponse;
import com.memo.file.service.FileService;
import com.memo.members.dto.JoinRequest;
import com.memo.members.dto.LoginRequest;
import com.memo.members.dto.ProfileUpdateRequest;
import com.memo.members.exception.DuplicateEmailException;
import com.memo.members.exception.DuplicateMemberException;
import com.memo.members.exception.LoginFailedException;
import com.memo.members.exception.MemberNotFoundException;
import com.memo.members.repository.MemberRepository;
import com.memo.members.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String join(JoinRequest request) {
        // 중복 체크
        if (memberRepository.findByUserId(request.getId()) != null) {
            throw new DuplicateMemberException();
        }
        if (memberRepository.findByEmail(request.getEmail()) != null) {
            throw new DuplicateEmailException();
        }

        // JoinRequest -> Members 변환
        Member member = new Member();
        member.setUserId(request.getId());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setEmail(request.getEmail());
        member.setNickname(request.getNickname());

        memberRepository.save(member);
        return jwtUtil.generateToken(member);
    }

    @Override
    public String login(LoginRequest request) {
        Member member = memberRepository.findByUserId(request.getId());
        if (member == null || !passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new LoginFailedException();
        }

        return jwtUtil.generateToken(member);
    }

    @Override
    public Member findById(int id) {
        return memberRepository.findById(id);
    }


    @Override
    public void updateProfile(int memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new MemberNotFoundException(memberId);
        }

        // 닉네임 업데이트
        member.setNickname(request.getNickname());

        // 프로필 이미지 업데이트
        if (request.hasProfileImage()) {
            updateProfileImage(member, request.getProfileImage());
        }

        memberRepository.updateProfile(member);
    }

    private void updateProfileImage(Member member, MultipartFile newImage) {
        // 기존 프로필 이미지 삭제
        if (member.hasProfileImage()) {
            fileService.deleteFile(member.getProfileImage(), FileType.PROFILE);
        }

        // 새 프로필 이미지 업로드
        FileResponse fileResponse = fileService.uploadFile(newImage, FileType.PROFILE);
        member.setProfileImage(fileResponse.getFilePath());
    }

    @Override
    public void deleteMember(int memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new MemberNotFoundException(memberId);
        }

        // 프로필 이미지가 있다면 삭제
        if (member.hasProfileImage()) {
            fileService.deleteFile(member.getProfileImage(), FileType.PROFILE);
        }

        member.delete();
        memberRepository.delete(memberId);
    }


}
