package com.example.board.domain.bookmark.controller;

import com.example.board.domain.bookmark.entity.Bookmark;
import com.example.board.domain.bookmark.service.BookmarkService;
import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/posts/{postId}/bookmark")
    public ResponseEntity<Void> createBookmark(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId
    ) {
        bookmarkService.createBookmark(user.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/unbookmark")
    public ResponseEntity<Void> deleteBookmark(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId
    ){
        bookmarkService.deleteBookmark(user.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my/bookmark")
    public ResponseEntity<Page<PostResponseDto>> getBookmarkPosts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(bookmarkService.findBookmarkedPostsByUserId(user.getUserId(), pageable));
    }
}
