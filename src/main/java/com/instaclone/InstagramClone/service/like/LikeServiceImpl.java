package com.instaclone.InstagramClone.service.like;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.instaclone.InstagramClone.dto.like.LikeResponseDto;
import com.instaclone.InstagramClone.entity.Like;
import com.instaclone.InstagramClone.entity.Post;
import com.instaclone.InstagramClone.entity.User;
import com.instaclone.InstagramClone.exception.ResourceNotFoundException;
import com.instaclone.InstagramClone.repository.LikeRepository;
import com.instaclone.InstagramClone.repository.PostRepository;
import com.instaclone.InstagramClone.repository.UserRepository;

@Service
public class LikeServiceImpl implements LikeService {
	private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LikeResponseDto toggleLikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        boolean liked;

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            liked = false;
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setPost(post);
            likeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            liked = true;
        }

        Post updatedPost = postRepository.save(post);
        return new LikeResponseDto(true, liked, updatedPost.getLikeCount());
    }
}
