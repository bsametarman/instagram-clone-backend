package com.instaclone.InstagramClone.service.fileStorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.instaclone.InstagramClone.config.FileStorageProperties;
import com.instaclone.InstagramClone.exception.FileNotFoundException;
import com.instaclone.InstagramClone.exception.FileStorageException;

@Service
public class FileStorageServiceImpl implements FileStorageService {
	private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private Path getTargetLocation(String subDirectory) {
        Path targetLocation = this.fileStorageLocation.resolve(subDirectory).normalize();
        try {
            Files.createDirectories(targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the sub-directory: " + subDirectory, ex);
        }
        return targetLocation;
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        Path targetDir = getTargetLocation(subDirectory);
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;

            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName, String subDirectory) {
        try {
            Path targetDir = getTargetLocation(subDirectory);
            Path filePath = targetDir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName + " in " + subDirectory);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName + " in " + subDirectory, ex);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path targetDir = getTargetLocation(subDirectory);
            Path filePath = targetDir.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Path getFileStorageLocation(String subDirectory) {
        return getTargetLocation(subDirectory);
    }
}
