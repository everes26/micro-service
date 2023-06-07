package com.status.api.service;

import com.status.api.request.PostRequest;
import com.status.api.response.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getPosts();
    PostResponse addPost(PostRequest post);
    Long deletePost(Long id);
}
