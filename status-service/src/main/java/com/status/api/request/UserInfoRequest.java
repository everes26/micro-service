package com.status.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoRequest {

    private Long id;
    private String profilePic;
}
