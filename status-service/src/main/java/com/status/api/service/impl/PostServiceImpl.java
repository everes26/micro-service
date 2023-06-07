package com.status.api.service.impl;

import com.status.api.entity.Post;
import com.status.api.enums.ErrorCode;
import com.status.api.exception.BasicException;
import com.status.api.repository.CommentRepository;
import com.status.api.repository.PostRepository;
import com.status.api.request.PostRequest;
import com.status.api.response.PostResponse;
import com.status.api.service.PostService;
import com.status.api.util.FunctionUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<PostResponse> getPosts() {
        return FunctionUtil.mapList(postRepository.findPostsByOrderByTimePostDesc(), PostResponse.class);
    }

    @Override
    public PostResponse addPost(PostRequest post) {
        Post request = Post.builder()
                .content(post.getContent())
                .userIdPost(post.getUserIdPost())
                .usernamePost(post.getUsernamePost())
                .build();
        return FunctionUtil.convertToEntity(postRepository.save(request), PostResponse.class);
    }

    @Override
    public Long deletePost(Long id) {
        postRepository.findById(id).orElseThrow(() -> new BasicException(ErrorCode.BAD_REQUEST.getCode(), "Post Id Not found"));
        commentRepository.deleteCommentsByPostId(id);
        return postRepository.deletePostById(id);
    }
}
