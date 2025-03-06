package com.unimate.filemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    
    private String userId;
    private String fileName;
    private String fileType;
    private String publicUrl;
    private LocalDateTime uploadedAt;
}