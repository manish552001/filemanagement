package com.unimate.filemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unimate.filemanagement.model.UserFile;

import java.util.List;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile, Long> {
    List<UserFile> findByUserIdOrderByUploadedAtDesc(String userId);
}