package com.status.api.response;

import lombok.Data;

import java.util.Date;

@Data
public class PostResponse {
    private Long id;

    private String content;

    private Date timePost;

    private Long totalComments;

    private Long totalFeelings;

    private Long userIdPost;

    private String usernamePost;
}
