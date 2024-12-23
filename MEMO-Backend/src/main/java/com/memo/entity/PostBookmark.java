package com.memo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostBookmark {
    private int id;
    private int userId;
    private int postId;
    private LocalDateTime createdAt;
}