package com.status.api.service;

import com.status.api.request.CommentRequest;
import com.status.api.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getComments(Long postId);
    CommentResponse addComment(CommentRequest comment);
    Long deleteComment(Long id);
}
