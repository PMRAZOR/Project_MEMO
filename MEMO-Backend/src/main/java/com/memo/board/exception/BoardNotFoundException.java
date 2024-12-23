package com.memo.board.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(Long id) {
        super("게시글을 찾을 수 없습니다. ID: " + id);
    }
}