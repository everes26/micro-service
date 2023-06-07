package com.status.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeelingPostResponse {
    private Long id;
    private Long postId;
    private Long userId;
}
