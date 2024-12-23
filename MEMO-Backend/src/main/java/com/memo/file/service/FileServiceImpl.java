package com.memo.file.service;

import com.memo.file.constant.FileType;
import com.memo.file.dto.FileResponse;
import com.memo.file.util.FileUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    @Value("${file.upload.base.path}")
    private String baseUploadPath;

    private final FileUtil fileUtil;

    @PostConstruct
    public void init() {
        try {
            Path rootPath = Paths.get(baseUploadPath);
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }

            // FileType에 정의된 모든 디렉토리 생성
            for (FileType type : FileType.values()) {
                Path typePath = Paths.get(baseUploadPath, type.getDirectory());
                if (!Files.exists(typePath)) {
                    Files.createDirectories(typePath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    @Override
    public FileResponse uploadFile(MultipartFile file, FileType fileType) {
        try {
            validateFile(file, fileType);
            String uploadPath = getUploadPath(fileType);
            String newFileName = saveFile(file, uploadPath);
            String filePath = fileType.getDirectory() + "/" + newFileName;

            return new FileResponse(true, "파일 업로드 성공", filePath, getFileUrl(newFileName, fileType));
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private String saveFile(MultipartFile file, String uploadPath) throws IOException {
        String newFileName = fileUtil.generateFileName(file.getOriginalFilename());
        Path directory = Paths.get(uploadPath);
        Path targetPath = directory.resolve(newFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    @Override
    public void deleteFile(String filePath, FileType fileType) {
        if (filePath != null) {
            try {
                Path file = Paths.get(getUploadPath(fileType), fileUtil.getFileName(filePath));
                Files.deleteIfExists(file);
            } catch (IOException e) {
                log.error("파일 삭제 실패: {}", filePath);
                throw new RuntimeException("파일 삭제에 실패했습니다.", e);
            }
        }
    }

    @Override
    public String getFileUrl(String fileName, FileType fileType) {
        return "/uploads/" + fileType.getDirectory() + "/" + fileName;
    }

    private String getUploadPath(FileType fileType) {
        return baseUploadPath + fileType.getDirectory() + "/";
    }

    private void validateFile(MultipartFile file, FileType fileType) {
        if (file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        if (file.getSize() > fileType.getMaxFileSize()) {
            throw new RuntimeException("파일 크기가 제한을 초과했습니다.");
        }

        String extension = fileUtil.getFileExtension(file.getOriginalFilename());
        if (!fileType.getAllowedExtensions().contains(extension.toLowerCase())) {
            throw new RuntimeException("지원하지 않는 파일 형식입니다.");
        }
    }
}