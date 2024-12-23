package com.memo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class VisitPhotos {
    private int id;
    private int visitId;
    private String filePath;
    private String createdAt;
}
