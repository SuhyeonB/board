package com.example.board.domain.post.dto;


import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        String title,
        String contents,
        String author,
        LocalDateTime createdAt,
        Long commentCount,
        Long likeCount
) {
}
