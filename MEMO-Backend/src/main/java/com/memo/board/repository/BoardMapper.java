package com.memo.board.repository;

import com.memo.board.dto.BoardSearchCondition;
import com.memo.entity.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    List<Board> findAll(@Param("condition") BoardSearchCondition condition,
            @Param("orderBy") String orderBy,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    long count(@Param("condition") BoardSearchCondition condition);

    Optional<Board> findById(Long id);

    void save(Board board);

    void update(Board board);

    void delete(Long id);

    void incrementViewCount(Long id);
}