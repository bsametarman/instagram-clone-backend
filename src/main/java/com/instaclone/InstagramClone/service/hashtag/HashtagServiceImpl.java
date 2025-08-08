package com.instaclone.InstagramClone.service.hashtag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.instaclone.InstagramClone.dto.hashtag.HashtagDto;
import com.instaclone.InstagramClone.entity.Hashtag;
import com.instaclone.InstagramClone.repository.HashtagRepository;
import com.instaclone.InstagramClone.repository.PostRepository;


@Service
public class HashtagServiceImpl implements HashtagService {
	private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;

    @Autowired
    public HashtagServiceImpl(HashtagRepository hashtagRepository, PostRepository postRepository) {
        this.hashtagRepository = hashtagRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public Set<Hashtag> findOrCreateHashtags(Set<String> hashtagNames) {
        Set<Hashtag> hashtags = new HashSet<>();
        for (String name : hashtagNames) {
            String cleanName = name.startsWith("#") ? name.substring(1).toLowerCase() : name.toLowerCase();
            if (cleanName.isEmpty()) continue;

            Hashtag hashtag = hashtagRepository.findByNameIgnoreCase(cleanName)
                    .orElseGet(() -> {
                        Hashtag newHashtag = new Hashtag();
                        newHashtag.setName(cleanName);
                        return hashtagRepository.save(newHashtag);
                    });
            hashtags.add(hashtag);
        }
        return hashtags;
    }

    @Override
    public List<HashtagDto> getPopularHashtags(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return hashtagRepository.findAll().stream()
                .map(this::convertToHashtagDto)
                .sorted((h1, h2) -> Integer.compare(h2.getPostCount(), h1.getPostCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private HashtagDto convertToHashtagDto(Hashtag hashtag) {
        HashtagDto dto = new HashtagDto();
        dto.setId(hashtag.getId());
        dto.setName(hashtag.getName());
        dto.setPostCount(0); // TODO: Calculate post count
        return dto;
    }
}
