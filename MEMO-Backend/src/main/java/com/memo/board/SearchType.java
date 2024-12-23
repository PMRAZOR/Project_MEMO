package com.memo.board;

public enum SearchType {
    TITLE("제목"),
    CONTENT("내용"),
    NICKNAME("작성자");

    private final String description;

    SearchType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}