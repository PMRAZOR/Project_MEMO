package com.memo.members.service;

import com.memo.entity.Member;
import com.memo.members.dto.JoinRequest;
import com.memo.members.dto.LoginRequest;
import com.memo.members.dto.ProfileUpdateRequest;


public interface MemberService {
    String join(JoinRequest members);

    String login(LoginRequest members);

    Member findById(int id);

    void deleteMember(int id);

    void updateProfile(int memberId, ProfileUpdateRequest request);
}
