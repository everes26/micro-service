package com.status.api.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class PostRequest {

    @NotNull(message = "Content can not null")
    private String content;

    @NotNull(message = "User Id Post can not null")
    private Long userIdPost;

    @NotNull(message = "Username Post can not null")
    private String usernamePost;

}
