package com.memo.board.service;

import com.memo.board.dto.BoardRequestDto;
import com.memo.board.dto.BoardResponseDto;
import com.memo.board.dto.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    /**
     * 게시글 목록 조회
     */
    Page<BoardResponseDto> getBoardList(BoardSearchCondition searchCondition, Pageable pageable);

    /**
     * 게시글 상세 조회
     */
    BoardResponseDto getBoard(Long id);

    /**
     * 게시글 생성
     */
    BoardResponseDto createBoard(BoardRequestDto requestDto);

    /**
     * 게시글 수정
     */
    BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto);

    /**
     * 게시글 삭제
     */
    void deleteBoard(Long id);
}