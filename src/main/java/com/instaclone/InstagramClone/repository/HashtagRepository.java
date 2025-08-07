package com.instaclone.InstagramClone.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.instaclone.InstagramClone.entity.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>{
	Optional<Hashtag> findByNameIgnoreCase(String name);
	Set<Hashtag> findByNameIn(List<String> names);
}
