package com.example.board.domain.comment.service;

import com.example.board.domain.comment.dto.CommentRequestDto;
import com.example.board.domain.comment.dto.CommentResponseDto;
import com.example.board.domain.comment.entity.Comment;
import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.user.entity.User;
import com.example.board.domain.user.repository.UserRepository;
import com.example.board.global.exception.DomainException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto saveComment(Long postId, Long userId, CommentRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 포스트입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."));

        if (post.isDeleted()) throw new DomainException(ErrorCode.POST_NOT_FOUND, "이미 삭제된 포스트입니다.");
        if (user.isDeleted()) throw new DomainException(ErrorCode.USER_NOT_FOUND, "이미 삭제된 사용자입니다.");

        Comment comment = Comment.builder()
                .contents(dto.getContents())
                .post(post)
                .user(user)
                .build();
        Comment saved =  commentRepository.save(comment);

        return new CommentResponseDto(saved.getId(), saved.getContents());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAllComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<CommentResponseDto> dtos = new ArrayList<>();

        for (Comment comment : comments) {
            dtos.add(new CommentResponseDto(comment.getId(), comment.getContents()));
        }

        return dtos;
    }

    @Transactional(readOnly = true)
    public CommentResponseDto findComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));
        return new CommentResponseDto(comment.getId(), comment.getContents());
    }

    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자 입니다."));

        if (user.isDeleted()) throw new DomainException(ErrorCode.USER_NOT_FOUND, "이미 삭제된 사용자입니다.");

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.INVALID_REQUEST, "작성자가 아니면 댓글을 수정할 수 없습니다.");
        }


        return  new CommentResponseDto(commentId, comment.getContents());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
