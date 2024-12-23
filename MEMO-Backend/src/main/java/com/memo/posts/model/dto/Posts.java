package com.memo.posts.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Posts {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String createdAt;
    private String keywords;
}
