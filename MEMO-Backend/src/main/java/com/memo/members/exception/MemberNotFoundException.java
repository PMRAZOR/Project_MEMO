package com.memo.members.exception;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException(int memberId) {
        super("회원을 찾을 수 없습니다. ID: " + memberId);
    }
}
