package com.status.api.request;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Data
public class CommentRequest {

    @NotNull(message = "Content can not null")
    private String content;

    @NotNull(message = "Post Id can not null")
    private Long postId;

    @NotNull(message = "User Id Comment can not null")
    private Long userIdComment;

    @NotNull(message = "Username Comment can not null")
    private String usernameComment;

}
