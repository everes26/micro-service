package com.status.api.service;

import com.status.api.request.UserStatusRequest;
import com.status.api.response.StatusResponse;
import com.status.api.response.UserStatusResponse;

import java.util.List;

public interface UserStatusService {
    Long addStatus(UserStatusRequest request);
    List<UserStatusResponse> getAllUserStatus();
}
