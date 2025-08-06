package com.instaclone.InstagramClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

}
