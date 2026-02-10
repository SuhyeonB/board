package com.example.board.domain.bookmark.repository;

import com.example.board.domain.bookmark.entity.Bookmark;
import com.example.board.domain.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

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
        from Bookmark b
        join b.post p
        join p.user u
        where b.user.id = :userId
          and p.deletedAt is null
    """)
    Page<PostResponseDto> findBookmarkedPostDtosByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
