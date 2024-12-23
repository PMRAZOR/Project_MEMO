package com.memo.members.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    private MultipartFile profileImage;

    public boolean hasProfileImage() {
        return profileImage != null && !profileImage.isEmpty();
    }
}