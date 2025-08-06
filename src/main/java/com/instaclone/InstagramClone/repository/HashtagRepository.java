package com.instaclone.InstagramClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>{

}
