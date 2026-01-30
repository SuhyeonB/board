package com.example.board.domain.like.controller;

import com.example.board.domain.like.service.LikeService;
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
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeService.likePost(postId, user.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeService.unlikePost(postId, user.getUserId());
        return ResponseEntity.ok().build();
    }
}
