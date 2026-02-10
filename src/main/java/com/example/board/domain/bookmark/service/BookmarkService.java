package com.example.board.domain.bookmark.service;

import com.example.board.domain.bookmark.entity.Bookmark;
import com.example.board.domain.bookmark.repository.BookmarkRepository;
import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.like.repository.LikeRepository;
import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public void createBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));

        if (user.isDeleted()) throw new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다.");
        if (post.isDeleted()) throw new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다.");

        if (bookmarkRepository.existsByUserIdAndPostId(userId, postId)) {
            return;
        }

        try {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .post(post)
                    .build();

            bookmarkRepository.save(bookmark);

        } catch (DataIntegrityViolationException e) {
            return;
        }
    }

    public void deleteBookmark(Long userId, Long postId) {
        bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findBookmarkedPostsByUserId(Long userId, Pageable pageable) {
        Page<PostResponseDto> posts =  bookmarkRepository.findBookmarkedPostDtosByUserId(userId, pageable);

        List<Long> postIds = posts.stream()
                .map(PostResponseDto::id)
                .toList();

        if(postIds.isEmpty()) { return posts; }

        // 댓글 수
        Map<Long, Long> commentCountMap =
                commentRepository.countCommentsByPostIds(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Long) row[1]
                        ));

        // 좋아요 수
        Map<Long, Long> likeCountMap =
                likeRepository.countLikesByPostIds(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Long) row[1]
                        ));

        return posts.map(dto -> new PostResponseDto(
                dto.id(),
                dto.title(),
                dto.contents(),
                dto.author(),
                dto.createdAt(),
                commentCountMap.getOrDefault(dto.id(), 0L),
                likeCountMap.getOrDefault(dto.id(), 0L)
        ));
    }
}
