package com.instaclone.InstagramClone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Like;
import com.instaclone.InstagramClone.entity.Post;
import com.instaclone.InstagramClone.entity.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{
	Optional<Like> findByUserAndPost(User user, Post post);
	long countByPost(Post post);
	void deleteByPost(Post post);
	List<Like> findByPost(Post post);
}
