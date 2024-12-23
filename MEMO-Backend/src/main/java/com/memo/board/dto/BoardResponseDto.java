package com.memo.board.dto;

import com.memo.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private int userId;
    private String nickname;
    private int viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .userId(board.getUserId())
                .title(board.getTitle())
                .content(board.getContent())
                .nickname(board.getNickname())
                .viewCount(board.getViewCount())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();
    }
}