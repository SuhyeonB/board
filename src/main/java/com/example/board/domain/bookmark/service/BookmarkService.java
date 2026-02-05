package com.example.board.domain.bookmark.service;

import com.example.board.domain.bookmark.entity.Bookmark;
import com.example.board.domain.bookmark.repository.BookmarkRepository;
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

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

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
        Page<Bookmark> bookmarks = bookmarkRepository.findBookmarkedPostsByUserId(userId, pageable);

        return bookmarks.map(
                bookmark -> {
                    Post post = bookmark.getPost();
                    return new PostResponseDto(
                            post.getId(),
                            post.getTitle(),
                            post.getContents(),
                            post.getUser().getNickname()
                    );
                }
        );
    }
}
