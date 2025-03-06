package com.unimate.filemanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.unimate.filemanagement.model.UserFile;
import java.util.List;

@Repository
public interface UserFileRepository extends MongoRepository<UserFile, String> {
    List<UserFile> findByUserIdOrderByUploadedAtDesc(String userId);
}
