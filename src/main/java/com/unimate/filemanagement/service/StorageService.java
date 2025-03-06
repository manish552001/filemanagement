package com.unimate.filemanagement.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.unimate.filemanagement.exception.FileStorageException;
import com.unimate.filemanagement.model.UserFile;
import com.unimate.filemanagement.repository.UserFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
    private UserFileRepository fileRepository;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${cdn.endpoint}")
    private String cdnEndpoint;
    
    public List<UserFile> uploadFiles(String userId, MultipartFile[] files) {
        List<UserFile> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Clean userId and filename
                String cleanUserId = userId.replaceAll("[\"\\\\]", "");
                String cleanFileName = file.getOriginalFilename()
                                                                .replaceAll("[\"\\\\]", "")
                                                                .replaceAll(" ", "_");

                // Generate unique filename
                String uniqueFileName = String.format("%s/%s_%s", 
                    cleanUserId,
                    UUID.randomUUID().toString(), 
                    cleanFileName);
                
                // Upload file to S3
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                
                s3Client.putObject(new PutObjectRequest(
                    bucketName, 
                    uniqueFileName, 
                    file.getInputStream(), 
                    metadata
                ));
                
                // Generate CDN URL
                String publicUrl = String.format("%s/%s", 
                    cdnEndpoint.trim(),
                    uniqueFileName);
                
                // Save file metadata in the database
                UserFile userFile = UserFile.builder()
                        .userId(cleanUserId)
                        .fileName(cleanFileName)
                        .fileType(file.getContentType())
                        .publicUrl(publicUrl)
                        .uploadedAt(LocalDateTime.now())
                        .build();
                
                fileRepository.save(userFile);
                log.info("File uploaded successfully. URL: {}", publicUrl);
                
                uploadedFiles.add(userFile);
            } catch (Exception e) {
                log.error("Error uploading file for user: {}", userId, e);
                throw new FileStorageException("Failed to upload file: " + e.getMessage());
            }
        }
        return uploadedFiles;
    }

    public List<UserFile> getUserFiles(String userId) {
        try {
            return fileRepository.findByUserIdOrderByUploadedAtDesc(userId);
        } catch (Exception e) {
            log.error("Error fetching files for user: {}", userId, e);
            throw new FileStorageException("Failed to fetch user files", e);
        }
    }
}
