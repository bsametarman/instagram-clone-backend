package com.instaclone.InstagramClone.service.hashtag;

import java.util.List;
import java.util.Set;

import com.instaclone.InstagramClone.dto.hashtag.HashtagDto;
import com.instaclone.InstagramClone.entity.Hashtag;

public interface HashtagService {
	Set<Hashtag> findOrCreateHashtags(Set<String> hashtagNames);
    List<HashtagDto> getPopularHashtags(int limit);
}
