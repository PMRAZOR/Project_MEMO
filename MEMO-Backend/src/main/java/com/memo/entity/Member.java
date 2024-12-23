package com.memo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member {
    private int id;
    private String userId;
    private String password;
    private String email;
    private String nickname;
    private String profileImage;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean hasProfileImage() {
        return profileImage != null && !profileImage.isBlank();
    }
}
