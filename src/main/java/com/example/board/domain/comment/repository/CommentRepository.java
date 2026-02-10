package com.example.board.domain.comment.repository;

import com.example.board.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    @Query("""
    select c.post.id, count(c)
    from Comment c
    where c.post.id in (:postIds)
    group by c.post.id
    """)
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);

    @Query("""
        select count(c)
        from Comment c
        where c.post.id = :postId
    """)
    long countByPostIdAndDeletedAtIsNull(@Param("postId") Long postId);
}
