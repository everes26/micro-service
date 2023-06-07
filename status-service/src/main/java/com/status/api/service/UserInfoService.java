package com.status.api.service;

import com.status.api.request.UserInfoRequest;
import com.status.api.response.UserInfoResponse;

public interface UserInfoService {
    UserInfoResponse addUserInfo(UserInfoRequest request);

    UserInfoResponse getUserInfo(Long id);
}
