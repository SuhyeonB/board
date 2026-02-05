package com.example.board.domain.post.service;

import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.post.dto.PostRequestDto;
import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponseDto savePost(Long id, PostRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        Post post = Post.builder()
                .title(dto.getTitle())
                .contents(dto.getContents())
                .user(user)
                .build();

        postRepository.save(post);

        return new PostResponseDto(post.getId(), post.getTitle(), post.getContents(), post.getUser().getNickname());
    }

    // N+1 문제 발생
    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByDeletedAtIsNull(pageable);

        return posts.map(post -> new PostResponseDto(
                post.getId(), post.getTitle(), post.getContents(), post.getUser().getNickname()
        ));
    }

    @Transactional(readOnly = true)
    public PostResponseDto findByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        if (post.isDeleted()) {
            throw new DomainException(ErrorCode.POST_NOT_FOUND, "이미 삭제된 포스트입니다.");
        }
        return new PostResponseDto(post.getId(), post.getTitle(), post.getContents(),  post.getUser().getNickname());
    }

    @Transactional
    public PostResponseDto updatePost(Long userId, Long postId, PostRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        if (post.isDeleted()) {
            throw new DomainException(ErrorCode.POST_NOT_FOUND, "이미 삭제된 포스트입니다.");
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "잘못된 접근입니다.");
        }

        post.update(dto.getTitle(), dto.getContents());

        return new  PostResponseDto(post.getId(), post.getTitle(), post.getContents(),  post.getUser().getNickname());
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (user.isDeleted()) {
            throw new DomainException(ErrorCode.FORBIDDEN, "삭제된 사용자입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        if  (post.isDeleted()) {
            throw new DomainException(ErrorCode.POST_NOT_FOUND, "이미 삭제된 포스트입니다.");
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "잘못된 접근입니다.");
        }

        // 하위 Comments 지우기
        post.delete();
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAllPostsByUserId(Pageable pageable, Long userId) {
        Page<Post> posts = postRepository.findByUserIdAndDeletedAtIsNull(pageable, userId);

        return posts.map(post -> new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContents(),
                post.getUser().getNickname()
        ));
    }
}
