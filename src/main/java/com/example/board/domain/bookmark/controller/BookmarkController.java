package com.example.board.domain.bookmark.controller;

import com.example.board.domain.bookmark.service.BookmarkService;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
