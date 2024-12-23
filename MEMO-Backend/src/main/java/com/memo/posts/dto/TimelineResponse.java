package com.memo.posts.dto;

import com.memo.posts.model.dto.Posts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TimelineResponse {
    private Posts post;
    private String thumbnailUrl; // 대표 이미지 URL
    private int visitCount;
}
