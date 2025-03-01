package com.unimate.filemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.unimate.filemanagement.dto.FileUploadResponse;
import com.unimate.filemanagement.dto.UserFileResponse;
import com.unimate.filemanagement.service.FileService;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadFile(userId, file));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFileResponse>> getUserFiles(
            @PathVariable String userId) {
        return ResponseEntity.ok(fileService.getUserFiles(userId));
    }
}