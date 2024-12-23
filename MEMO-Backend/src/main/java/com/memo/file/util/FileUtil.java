package com.memo.file.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

// FileUtil.java
@Component
public class FileUtil {
    public String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + getFileExtension(originalFilename);
    }

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    public String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
}