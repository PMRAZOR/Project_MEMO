package com.memo.members.exception;

public class DuplicateMemberException extends MemberException {
    public static final String MESSAGE = "이미 존재하는 회원입니다.";

    public DuplicateMemberException() {
        super(MESSAGE);
    }
}