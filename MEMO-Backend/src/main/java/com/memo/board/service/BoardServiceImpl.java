package com.memo.board.service;

import com.memo.board.dto.BoardRequestDto;
import com.memo.board.dto.BoardResponseDto;
import com.memo.board.dto.BoardSearchCondition;
import com.memo.board.exception.BoardNotFoundException;
import com.memo.board.repository.BoardMapper;
import com.memo.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;

    @Override
    public Page<BoardResponseDto> getBoardList(
            BoardSearchCondition condition,
            Pageable pageable) {

        // 정렬 조건 생성
        String orderBy = createOrderBy(pageable);

        // 페이징 계산
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();

        // 데이터 조회
        List<Board> boards = boardMapper.findAll(condition, orderBy, offset, pageSize);
        long total = boardMapper.count(condition);

        // DTO 변환
        List<BoardResponseDto> content = boards.stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    private String createOrderBy(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            List<String> orders = new ArrayList<>();

            pageable.getSort().forEach(order -> {
                String property = order.getProperty();
                String direction = order.getDirection().name();
                orders.add(property + " " + direction);
            });

            return String.join(", ", orders);
        }

        return "created_date DESC";
    }

    @Override
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        Board board = requestDto.toEntity();
        boardMapper.save(board);
        return BoardResponseDto.from(board);
    }

    @Override
    @Transactional
    public BoardResponseDto getBoard(Long id) {
        Board board = boardMapper.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));
        incrementViewCount(id);
        return BoardResponseDto.from(board);
    }

    @Override
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto) {
        Board board = boardMapper.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));

        board.update(requestDto.getTitle(), requestDto.getContent());
        boardMapper.update(board);

        return BoardResponseDto.from(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        boardMapper.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));

        boardMapper.delete(id);
    }

    @Transactional
    private void incrementViewCount(Long id) {
        Board board = boardMapper.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));
        boardMapper.incrementViewCount(id);
    }

}