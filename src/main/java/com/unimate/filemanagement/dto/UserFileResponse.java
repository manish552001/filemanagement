package com.unimate.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserFileResponse {
    private String fileName;
    private String fileType;
    private String publicUrl;
    private LocalDateTime uploadedAt;
}