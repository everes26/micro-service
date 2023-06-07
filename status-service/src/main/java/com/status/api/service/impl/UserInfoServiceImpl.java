package com.status.api.service.impl;

import com.status.api.entity.UserInfo;
import com.status.api.enums.ErrorCode;
import com.status.api.exception.BasicException;
import com.status.api.repository.UserInfoRepository;
import com.status.api.request.UserInfoRequest;
import com.status.api.response.UserInfoResponse;
import com.status.api.service.UserInfoService;
import com.status.api.util.FunctionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    @Override
    public UserInfoResponse addUserInfo(UserInfoRequest request) {
        if (!userInfoRepository.existsById(request.getId())) {
            UserInfo userInfo = userInfoRepository.save(new UserInfo(request.getId()));
            return new UserInfoResponse(userInfo.getId(), userInfo.getProfilePic());
        } else {
            UserInfo userInfo = userInfoRepository.findById(request.getId()).get();
            if (!StringUtils.isBlank(request.getProfilePic())) {
                userInfo.setProfilePic(request.getProfilePic());
                UserInfo info =  userInfoRepository.save(userInfo);
                return new UserInfoResponse(info.getId(), info.getProfilePic());
            } else return new UserInfoResponse(userInfo.getId(), userInfo.getProfilePic());
        }
    }


    @Override
    public UserInfoResponse getUserInfo(Long id) {
        UserInfo userInfo = userInfoRepository.findById(id).orElseThrow(() ->
                new BasicException(ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage()));
        return new UserInfoResponse(userInfo.getId(), userInfo.getProfilePic());
    }
}
