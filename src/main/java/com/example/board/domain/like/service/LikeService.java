package com.example.board.domain.like.service;

import com.example.board.domain.like.entity.Like;
import com.example.board.domain.like.repository.LikeRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    @Transactional
    public void likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        if (user.isDeleted()) throw new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다.");
        if (post.isDeleted()) throw new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다.");

        // 1) 1차 방어: 이미 좋아요면 그냥 성공(멱등)
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            return;
        }

        // 2) 2차 방어(동시성): exists 통과 후 동시에 save하면 유니크 제약으로 예외가 날 수 있음
        try {
            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();

            likeRepository.save(like);

        } catch (DataIntegrityViolationException e) {
            // (user_id, post_id) 유니크 제약 위반일 가능성이 큼
            // A안 정책: "이미 좋아요 상태"로 간주하고 조용히 성공 처리
            return;
        }
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
    }
}
