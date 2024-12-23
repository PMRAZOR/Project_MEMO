package com.memo.photos.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Photos {
    private Integer id;
    private Integer visitId;
    private String fileName;
    private String originalName;
    private String filePath;
    private LocalDateTime createdAt;
}
