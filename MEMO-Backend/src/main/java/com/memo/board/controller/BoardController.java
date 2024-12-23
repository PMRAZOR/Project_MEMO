package com.memo.board.controller;

import com.memo.board.SearchType;
import com.memo.board.dto.BoardRequestDto;
import com.memo.board.dto.BoardResponseDto;
import com.memo.board.dto.BoardSearchCondition;
import com.memo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getBoards(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "createdDate") String sortField,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            @PageableDefault(size = 10) Pageable pageable) {

        BoardSearchCondition searchCondition = BoardSearchCondition.builder()
                .searchType(searchType)
                .searchKeyword(searchKeyword)
                .build();

        return ResponseEntity.ok(boardService.getBoardList(searchCondition, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardRequestDto requestDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}