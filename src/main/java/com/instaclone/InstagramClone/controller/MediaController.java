package com.instaclone.InstagramClone.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.instaclone.InstagramClone.service.post.PostService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/media")
public class MediaController {
	private final PostService postService;

    @Autowired
    public MediaController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts/image/{postId}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId) {
        byte[] image = postService.getPostImageContent(postId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); 

        headers.setContentLength(image.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(image);
    }

    @GetMapping("/posts/video/{fileName:.+}")
    public ResponseEntity<Resource> getPostVideo(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = postService.getVideoContent(fileName);

        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        	
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
