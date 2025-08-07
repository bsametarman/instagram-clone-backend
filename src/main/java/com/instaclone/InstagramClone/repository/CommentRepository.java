package com.instaclone.InstagramClone.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Comment;
import com.instaclone.InstagramClone.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
	List<Comment> findByPostOrderByCreatedAtAsc(Post post);
	Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);
}
