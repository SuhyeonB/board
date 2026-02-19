package com.example.board.domain.post.service;

import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostImage;
import com.example.board.domain.post.repository.PostImageRepository;
import com.example.board.global.util.HtmlImageExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostMediaService {

    private final PostImageRepository postImageRepository;

    public void sync(Post post, String htmlContent) {
        if (post.getId() == null) {
            throw new IllegalStateException("Post must be saved before syncing images.");
        }

        List<String> keys = HtmlImageExtractor.extractKeys(htmlContent);

        postImageRepository.deleteByPostId(post.getId());

        for (int i = 0; i < keys.size(); i++) {
            PostImage image = PostImage.of(post, keys.get(i), i);
            postImageRepository.save(image);
        }
    }


}
