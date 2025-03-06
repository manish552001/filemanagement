package com.unimate.filemanagement.controller;

import com.unimate.filemanagement.exception.FileStorageException;
import com.unimate.filemanagement.model.UserFile;
import com.unimate.filemanagement.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {
    
    @Autowired
    private StorageService storageService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("userId") String userId,
            @RequestParam("files") MultipartFile[] files) {
        log.info("Upload request received for user: {}", userId);
        
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No file provided");
        }
        
        try {
            List<UserFile> uploadedFiles = storageService.uploadFiles(userId, files);
            return ResponseEntity.ok(uploadedFiles);
        } catch (FileStorageException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFile>> getUserFiles(@PathVariable String userId) {
        log.info("Fetching files for user: {}", userId);
        return ResponseEntity.ok(storageService.getUserFiles(userId));
    }
}
