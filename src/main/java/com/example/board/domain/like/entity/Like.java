package com.example.board.domain.like.entity;

import com.example.board.domain.post.entity.Post;
import com.example.board.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_like_user_post", columnNames = {"user_id", "post_id"})
        }
)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
