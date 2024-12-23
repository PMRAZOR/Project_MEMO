package com.memo.members.exception;

public class LoginFailedException extends MemberException {
    public static final String MESSAGE = "아이디 또는 비밀번호가 잘못되었습니다.";

    public LoginFailedException() {
        super(MESSAGE);  // AuthException의 생성자 호출
    }
}
