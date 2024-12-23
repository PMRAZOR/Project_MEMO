package com.memo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Users {
    private int id;
    private String email;
    private String nickname;
    private String profileImage;
    private String createdAt;
}
