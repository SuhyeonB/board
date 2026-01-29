package com.example.board.domain.post.controller;

import com.example.board.domain.post.dto.PostRequestDto;
import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.domain.post.service.PostService;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost (
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody PostRequestDto dto
    ) {
        return ResponseEntity.ok(postService.savePost(user.getUserId(), dto));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.findByPostId(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId,
            @RequestBody PostRequestDto dto
    ) {
        return ResponseEntity.ok(postService.updatePost(user.getUserId(), postId, dto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId
    ) {
        postService.deletePost(user.getUserId(), postId);
        return ResponseEntity.ok().build();
    }
}
