package com.memo.file.constant;

import java.util.Arrays;
import java.util.List;

// FileType.java
public enum FileType {
    PROFILE("profiles", Arrays.asList(".jpg", ".jpeg", ".png", ".webp"), 5 * 1024 * 1024L);
//    BOARD("boards", Arrays.asList(".jpg", ".jpeg", ".png", ".gif"), 10 * 1024 * 1024L),
//    NOTICE("notices", Arrays.asList(".jpg", ".jpeg", ".png", ".pdf"), 20 * 1024 * 1024L);

    private final String directory;
    private final List<String> allowedExtensions;
    private final long maxFileSize;

    FileType(String directory, List<String> allowedExtensions, long maxFileSize) {
        this.directory = directory;
        this.allowedExtensions = allowedExtensions;
        this.maxFileSize = maxFileSize;
    }

    public String getDirectory() {
        return directory;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }
}