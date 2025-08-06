package com.instaclone.InstagramClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{

}
