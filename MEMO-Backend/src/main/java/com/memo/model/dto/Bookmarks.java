package com.memo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Bookmarks {
    private int id;
    private int userId;
    private int visitId;
    private String createdAt;
}
