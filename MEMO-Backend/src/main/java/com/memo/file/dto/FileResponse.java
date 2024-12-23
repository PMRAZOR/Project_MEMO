package com.memo.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// FileResponse.java
// FileResponse.java
@Getter
@AllArgsConstructor
public class FileResponse {
    private boolean success;
    private String message;
    private String filePath;
    private String fileUrl;
}