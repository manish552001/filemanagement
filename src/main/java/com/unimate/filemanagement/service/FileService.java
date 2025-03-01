package com.unimate.filemanagement.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unimate.filemanagement.dto.FileUploadResponse;
import com.unimate.filemanagement.dto.UserFileResponse;
import com.unimate.filemanagement.exception.FileStorageException;
import com.unimate.filemanagement.model.UserFile;
import com.unimate.filemanagement.repository.UserFileRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService {
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private UserFileRepository fileRepository;
    
    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                
                // Set bucket policy to public
                String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": "s3:GetObject",
                                "Resource": "arn:aws:s3:::%s/*"
                            }
                        ]
                    }
                    """.formatted(bucketName);

                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build()
                );
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket", e);
            throw new FileStorageException("Could not initialize storage", e);
        }
    }

    public FileUploadResponse uploadFile(String userId, MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file");
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // Upload to MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uniqueFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // Generate public URL
            String publicUrl = String.format("%s/%s/%s", minioEndpoint, bucketName, uniqueFileName);

            // Save metadata to database
            UserFile userFile = UserFile.builder()
                    .userId(userId)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .publicUrl(publicUrl)
                    .uploadedAt(LocalDateTime.now())
                    .build();
            
            fileRepository.save(userFile);

            log.info("File uploaded successfully: {}", uniqueFileName);
            return new FileUploadResponse(publicUrl);

        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    public List<UserFileResponse> getUserFiles(String userId) {
        try {
            return fileRepository.findByUserIdOrderByUploadedAtDesc(userId)
                    .stream()
                    .map(file -> new UserFileResponse(
                            file.getFileName(),
                            file.getFileType(),
                            file.getPublicUrl(),
                            file.getUploadedAt()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving user files for userId: {}", userId, e);
            throw new FileStorageException("Failed to retrieve user files", e);
        }
    }
}