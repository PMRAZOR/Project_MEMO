package com.memo.file.exception;

import com.memo.file.dto.FileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// FileExceptionHandler.java
@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<FileResponse> handleFileUploadException(FileUploadException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new FileResponse(false, e.getMessage(), null, null));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<FileResponse> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new FileResponse(false, "파일 크기가 제한을 초과했습니다.", null, null));
    }
}