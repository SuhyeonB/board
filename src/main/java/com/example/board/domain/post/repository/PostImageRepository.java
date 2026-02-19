package com.example.board.domain.post.repository;

import com.example.board.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    void deleteByPostId(Long postId);
}
