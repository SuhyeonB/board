package com.example.board.domain.post.repository;

import com.example.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDeletedAtIsNull(Pageable pageable);
    List<Post> findByUserId(Long userId);

    Page<Post> findByUserIdAndDeletedAtIsNull(Pageable pageable, Long userId);
}
