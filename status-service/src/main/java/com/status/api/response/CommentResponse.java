package com.status.api.response;


import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private Long id;

    private String content;

    private Long postId;

    private Date timeComment;

    private Long userIdComment;

    private String usernameComment;

}
