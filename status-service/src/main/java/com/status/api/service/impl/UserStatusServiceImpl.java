package com.status.api.service.impl;

import com.status.api.entity.Status;
import com.status.api.entity.UserInfo;
import com.status.api.entity.UserStatus;
import com.status.api.repository.StatusRepository;
import com.status.api.repository.UserInfoRepository;
import com.status.api.repository.UserStatusReposetory;
import com.status.api.request.UserStatusRequest;
import com.status.api.response.StatusResponse;
import com.status.api.response.UserStatusResponse;
import com.status.api.service.UserStatusService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserStatusServiceImpl implements UserStatusService {
    private final UserStatusReposetory userStatusReposetory;
    private final UserInfoRepository userInfoRepository;
    private final StatusRepository statusRepository;

    public UserStatusServiceImpl(UserStatusReposetory userStatusReposetory, UserInfoRepository userInfoRepository, StatusRepository statusRepository) {
        this.userStatusReposetory = userStatusReposetory;
        this.userInfoRepository = userInfoRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public Long addStatus(UserStatusRequest request) {

        if (userStatusReposetory.existsByUserId(request.getUserId())) {
            UserStatus entity = userStatusReposetory.findById(request.getUserId()).get();
            Status status = statusRepository.save(new Status(request.getImageUrl(), entity));
            return status.getId();
        } else {
            UserStatus userStatus = new UserStatus();
            userStatus.setUserId(request.getUserId());
            userStatus.setLastUpdated(new Date());
            Status status = statusRepository.save(new Status(request.getImageUrl(), userStatus));
            return status.getId();
        }
    }

    @Override
    public List<UserStatusResponse> getAllUserStatus() {
        List<UserStatusResponse> responseList = new ArrayList<>();
        List<UserStatus> userStatusList = userStatusReposetory.findAll();
        if (!CollectionUtils.isEmpty(userStatusList)) {
            userStatusList.forEach(userStatus -> {
                UserInfo userInfo = userInfoRepository.findById(userStatus.getUserId()).get();
                UserStatusResponse statusResponse = new UserStatusResponse(userStatus.getUserId(), userInfo.getProfilePic(),
                        userStatus.getLastUpdated(), userStatus.getStatuses());
                responseList.add(statusResponse);
            });
        }
        return responseList;
    }
}
