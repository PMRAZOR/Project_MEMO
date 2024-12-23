package com.memo.posts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/file-check")
    public ResponseEntity<Map<String, Object>> checkFileSystem() {
        Map<String, Object> result = new HashMap<>();

        // 1. 업로드 디렉토리 정보 확인
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File directory = new File(uploadDir);

        result.put("uploadPath", uploadDir);
        result.put("directoryExists", directory.exists());
        result.put("isDirectory", directory.isDirectory());
        result.put("canRead", directory.canRead());

        // 2. 디렉토리 내 파일 목록
        if (directory.exists() && directory.isDirectory()) {
            List<Map<String, Object>> files = new ArrayList<>();
            File[] fileList = directory.listFiles();

            if (fileList != null) {
                for (File file : fileList) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("size", file.length());
                    fileInfo.put("canRead", file.canRead());
                    fileInfo.put("absolutePath", file.getAbsolutePath());
                    files.add(fileInfo);
                }
            }
            result.put("files", files);
        }

        // 3. 현재 리소스 핸들러 설정 정보
        result.put("resourceHandlerPath", "/uploads/**");
        result.put("resourceLocation", "file:///" + uploadDir);

        return ResponseEntity.ok(result);
    }
}