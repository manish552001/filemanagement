package com.unimate.filemanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "user_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {
    @Id
    private String id; // MongoDB typically uses a String ID (ObjectId)

    private String userId;
    private String fileName;
    private String fileType;
    private String publicUrl;
    private LocalDateTime uploadedAt;
}
