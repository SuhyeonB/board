package com.example.board.domain.bookmark.repository;

import com.example.board.domain.bookmark.entity.Bookmark;
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
        select b
        from Bookmark b
        join fetch b.post p
        join fetch p.user
        where b.user.id = :userId
          and p.deletedAt is null
    """)
    Page<Bookmark> findBookmarkedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
