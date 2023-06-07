package com.status.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class StatusResponse {
    private String imageUrl;
    private Date timeStamp;

}
