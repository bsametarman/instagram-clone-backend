package com.instaclone.InstagramClone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.MediaType;
import com.instaclone.InstagramClone.entity.Post;
import com.instaclone.InstagramClone.entity.User;

@Repository
public interface PostRepostitory extends JpaRepository<Post, Long>{
	List<Post> findByUserOrderByCreatedAtDesc(User user);
	Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	Page<Post> findByUserAndMediaTypeOrderByCreatedAtDesc(User user, MediaType mediaType, Pageable pageable);
	@Query("SELECT p.imageData FROM Post p WHERE p.id = :postId")
    Optional<byte[]> findImageDataById(@Param("postId") Long postId);
    
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.hashtags h " +
            "WHERE p.user = :user AND p.mediaType = :mediaType " +
            "AND (" +
            "   :searchTerm IS NULL OR :searchTerm = '' OR " +
            "   LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "   LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "   LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            ")")
     Page<Post> findByUserAndMediaTypeAndSearchTerm(
             @Param("user") User user,
             @Param("mediaType") MediaType mediaType,
             @Param("searchTerm") String searchTerm,
             Pageable pageable
     );
    
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.hashtags h WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
     Page<Post> findAllBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}
