package com.example.board.domain.post.entity;

import com.example.board.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 500)
    private String storageKey;

    @Column(nullable = false)
    private int sortOrder;

    public static PostImage of(Post post, String storageKey, int sortOrder) {
        PostImage image = new PostImage();
        image.post = post;
        image.storageKey = storageKey;
        image.sortOrder = sortOrder;
        return image;
    }
}
