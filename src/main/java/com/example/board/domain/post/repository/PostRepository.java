package com.example.board.domain.post.repository;

import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDeletedAtIsNull(Pageable pageable);
    List<Post> findByUserId(Long userId);

    @Query("""
    select new com.example.board.domain.post.dto.PostResponseDto(
        p.id, p.title, p.contents, u.nickname, p.createdAt, 0L, 0L
    )
    from Post p
    join p.user u
    where p.deletedAt is null
      and u.id = :userId
""")
    Page<PostResponseDto> findPostDtosByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
    select new com.example.board.domain.post.dto.PostResponseDto(
        p.id,
        p.title,
        p.contents,
        u.nickname,
        p.createdAt,
        0L,
        0L
    )
    from Post p
    join p.user u
    where p.deletedAt is null
""")
    Page<PostResponseDto> findPostDtosByDeletedAtIsNull(Pageable pageable);

    @Query("""
    select new com.example.board.domain.post.dto.PostResponseDto(
        p.id,
        p.title,
        p.contents,
        u.nickname,
        p.createdAt,
        0L,
        0L
    )
    from Post p
    join p.user u
    where p.id = :postId
      and p.deletedAt is null
""")
    Optional<PostResponseDto> findPostDtoById(@Param("postId") Long postId);

}
