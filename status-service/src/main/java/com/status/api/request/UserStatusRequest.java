package com.status.api.request;

import lombok.Data;

@Data
public class UserStatusRequest {
    private Long userId;
    private String imageUrl;
}
