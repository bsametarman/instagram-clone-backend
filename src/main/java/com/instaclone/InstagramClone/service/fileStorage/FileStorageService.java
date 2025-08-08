package com.instaclone.InstagramClone.service.fileStorage;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	String storeFile(MultipartFile file, String subDirectory);
    Resource loadFileAsResource(String fileName, String subDirectory);
    void deleteFile(String fileName, String subDirectory);
    Path getFileStorageLocation(String subDirectory);
}
