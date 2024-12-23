package com.memo.file.service;

import com.memo.file.constant.FileType;
import com.memo.file.dto.FileResponse;

import org.springframework.web.multipart.MultipartFile;

// FileService.java
public interface FileService {
    FileResponse uploadFile(MultipartFile file, FileType fileType);
    void deleteFile(String filePath, FileType fileType);
    String getFileUrl(String fileName, FileType fileType);
}