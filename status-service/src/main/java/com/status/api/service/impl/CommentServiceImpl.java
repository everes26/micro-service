package com.status.api.service.impl;

import com.status.api.entity.Comment;
import com.status.api.entity.Post;
import com.status.api.enums.ErrorCode;
import com.status.api.exception.BasicException;
import com.status.api.repository.CommentRepository;
import com.status.api.repository.PostRepository;
import com.status.api.request.CommentRequest;
import com.status.api.response.CommentResponse;
import com.status.api.service.CommentService;
import com.status.api.util.FunctionUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<CommentResponse> getComments(Long postId) {
        return FunctionUtil.mapList(commentRepository.findCommentsByPostIdOrderByTimeCommentAsc(postId), CommentResponse.class);
    }

    @Override
    public CommentResponse addComment(CommentRequest comment) {
        Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new BasicException(ErrorCode.BAD_REQUEST.getCode(), "Post Id not found"));
        post.setTotalComments(post.getTotalComments() + 1);
        postRepository.save(post);
        Comment request = Comment.builder()
                .postId(comment.getPostId())
                .content(comment.getContent())
                .userIdComment(comment.getUserIdComment())
                .usernameComment(comment.getUsernameComment())
                .build();
        return FunctionUtil.convertToEntity(
                commentRepository.save(request), CommentResponse.class);
    }

    @Override
    public Long deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new BasicException(ErrorCode.BAD_REQUEST.getCode(), "Comment not found"));
        Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new BasicException(ErrorCode.BAD_REQUEST.getCode(), "Post Id not found"));
        post.setTotalComments(post.getTotalComments() + 1);
        return commentRepository.deleteCommentById(id);
    }
}
