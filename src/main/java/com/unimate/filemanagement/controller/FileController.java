package com.unimate.filemanagement.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.unimate.filemanagement.dto.FileUploadResponse;
import com.unimate.filemanagement.dto.UserFileResponse;
import com.unimate.filemanagement.service.StorageService;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {
    
    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) {
        log.info("File upload request received for user: {}", userId);
        return ResponseEntity.ok(storageService.uploadFile(userId, file));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFileResponse>> getUserFiles(
            @PathVariable String userId) {
        log.info("Fetching files for user: {}", userId);
        return ResponseEntity.ok(storageService.getUserFiles(userId));
    }
}