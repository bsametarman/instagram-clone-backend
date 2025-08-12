package com.instaclone.InstagramClone.service.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.instaclone.InstagramClone.dto.comment.CommentResponseDto;
import com.instaclone.InstagramClone.dto.comment.CreateCommentRequestDto;
import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.entity.Comment;
import com.instaclone.InstagramClone.entity.Post;
import com.instaclone.InstagramClone.entity.User;
import com.instaclone.InstagramClone.exception.ResourceNotFoundException;
import com.instaclone.InstagramClone.exception.UnauthorizedActionException;
import com.instaclone.InstagramClone.repository.CommentRepository;
import com.instaclone.InstagramClone.repository.PostRepository;
import com.instaclone.InstagramClone.repository.UserRepository;
import com.instaclone.InstagramClone.service.user.UserServiceImpl;

@Service
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              UserServiceImpl userServiceImpl) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    @Transactional
    public CommentResponseDto addCommentToPost(Long postId, CreateCommentRequestDto commentRequestDto, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setPost(post);
        comment.setUser(user);
        
        Comment savedComment = commentRepository.save(comment);
        
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        
        return convertToCommentResponseDto(savedComment);
    }

    @Override
    public Page<CommentResponseDto> getCommentsByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        return comments.map(this::convertToCommentResponseDto);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!comment.getUser().getId().equals(currentUser.getId()) &&
            !comment.getPost().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not authorized to delete this comment.");
        }
        
        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
        
        commentRepository.delete(comment);
    }


    private CommentResponseDto convertToCommentResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());

        UserSummaryDto userSummaryDto = userServiceImpl.convertToUserSummaryDto(comment.getUser());
        dto.setUser(userSummaryDto);
        return dto;
    }

	@Override
	public long countByPost(Post post) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteAll(Iterable<? extends Comment> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Comment> findByPost(Post post) {
		// TODO Auto-generated method stub
		return null;
	}
}
