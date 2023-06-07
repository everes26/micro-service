package com.status.api.service;

import com.status.api.request.FeelingPostRequest;
import com.status.api.response.FeelingPostResponse;

public interface FeelingPostService {
    FeelingPostResponse addFeelingPost(FeelingPostRequest request);
    void removeFeelingPost(FeelingPostRequest request);
    boolean existsByPostIdAndUserId(FeelingPostRequest request);
}
