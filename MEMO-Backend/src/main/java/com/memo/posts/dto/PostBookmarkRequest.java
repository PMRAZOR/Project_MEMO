package com.memo.posts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostBookmarkRequest {

    @NotNull
    private int userId;

    @NotNull
    private int postId;
}
