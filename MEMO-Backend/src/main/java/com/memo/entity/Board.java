package com.memo.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    private Long id;
    private String title;
    private String content;
    private int userId;
    private String nickname;    // JOIN 결과를 담기 위한 필드
    private int viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public Board(String title, String content, int userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.viewCount = 0;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedDate = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}