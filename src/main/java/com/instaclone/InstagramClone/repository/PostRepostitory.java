package com.instaclone.InstagramClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Post;

@Repository
public interface PostRepostitory extends JpaRepository<Post, Long>{

}
