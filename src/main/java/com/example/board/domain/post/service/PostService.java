package com.example.board.domain.post.service;

import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.post.dto.PostRequestDto;
import com.example.board.domain.post.dto.PostResponseDto;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponseDto savePost(PostRequestDto dto) {
        Post post = new Post(dto.getTitle(), dto.getContents());
        Post saved =  postRepository.save(post);

        return new PostResponseDto(saved.getId(), saved.getTitle(), saved.getContents());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostResponseDto> dtos = new ArrayList<>();

        for (Post post : posts) {
            dtos.add(new PostResponseDto(post.getId(), post.getTitle(), post.getContents()));
        }

        return dtos;
    }

    @Transactional(readOnly = true)
    public PostResponseDto findByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + postId + " does not exist"));

        return new PostResponseDto(post.getId(), post.getTitle(), post.getContents());
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + postId + " does not exist"));

        post.update(dto.getTitle(), dto.getContents());

        return new  PostResponseDto(post.getId(), post.getTitle(), post.getContents());
    }

    @Transactional
    public void deletePost(Long postId) {
        commentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }
}
