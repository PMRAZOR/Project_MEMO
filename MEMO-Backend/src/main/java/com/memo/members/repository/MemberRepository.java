package com.memo.members.repository;

import com.memo.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberRepository {

    int save(Member member);

    Member findByUserId(String userId);

    Member findById(int id);

    Member findByEmail(String email);

    void updateProfile(Member member);

    void delete(int id);
}
