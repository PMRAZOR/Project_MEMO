package com.memo.members.exception;

public class MemberException extends RuntimeException {
    public MemberException(String message) {
        super(message);  // RuntimeException의 생성자 호출
    }
}
