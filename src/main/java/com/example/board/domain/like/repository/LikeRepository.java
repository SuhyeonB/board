package com.example.board.domain.like.repository;

import com.example.board.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    @Query("""
        select l.post.id, count(l)
        from Like l
        where l.post.id in (:postIds)
        group by l.post.id
    """)
    List<Object[]> countLikesByPostIds(@Param("postIds") List<Long> postIds);

    @Query("""
        select count(l)
        from Like l
        where l.post.id = :postId
    """)
    long countByPostId(@Param("postId") Long postId);
}
