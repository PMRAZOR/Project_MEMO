package com.memo.board.dto;

import com.memo.board.SearchType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardSearchCondition {
    private SearchType searchType;
    private String searchKeyword;
}