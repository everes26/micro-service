package com.status.api.service.impl;

import com.status.api.entity.FeelingPost;
import com.status.api.entity.Post;
import com.status.api.enums.ErrorCode;
import com.status.api.exception.BasicException;
import com.status.api.repository.FeelingPostRepository;
import com.status.api.repository.PostRepository;
import com.status.api.request.FeelingPostRequest;
import com.status.api.response.FeelingPostResponse;
import com.status.api.service.FeelingPostService;
import org.springframework.stereotype.Service;

@Service
public class FeelingPostServiceImpl implements FeelingPostService {

    private final FeelingPostRepository feelingPostRepository;
    private final PostRepository postRepository;

    public FeelingPostServiceImpl(FeelingPostRepository feelingPostRepository, PostRepository postRepository) {
        this.feelingPostRepository = feelingPostRepository;
        this.postRepository = postRepository;
    }

    @Override
    public FeelingPostResponse addFeelingPost(FeelingPostRequest request) {
        if (postRepository.existsById(request.getPostId())) {
            Post post = postRepository.findById(request.getPostId()).get();
            FeelingPost feelingPost = feelingPostRepository.save(new FeelingPost(request.getPostId(), request.getUserId()));
            post.setTotalFeelings(post.getTotalFeelings() + 1);
            postRepository.save(post);
            return new FeelingPostResponse(feelingPost.getId(), feelingPost.getPostId(), feelingPost.getUserId());
        } else throw new BasicException(ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }

    @Override
    public void removeFeelingPost(FeelingPostRequest request) {
        if (postRepository.existsById(request.getPostId())) {
            FeelingPost feelingPost = feelingPostRepository.findFeelingPostByPostIdAndUserId(request.getPostId(), request.getUserId());
            feelingPostRepository.deleteById(feelingPost.getId());
            Post post = postRepository.findById(request.getPostId()).get();
            post.setTotalFeelings(post.getTotalFeelings() - 1);
            postRepository.save(post);

        } else throw new BasicException(ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());

    }

    @Override
    public boolean existsByPostIdAndUserId(FeelingPostRequest request) {
        return feelingPostRepository.existsByPostIdAndUserId(request.getPostId(), request.getUserId());
    }
}
