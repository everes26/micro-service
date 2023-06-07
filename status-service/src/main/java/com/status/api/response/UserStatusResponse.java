package com.status.api.response;

import com.status.api.entity.Status;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserStatusResponse {
    private Long userId;
    private String profileImage;
    private Date lastUpdated;
    private List<StatusResponse> statuses;

    public UserStatusResponse(Long userId, String profileImage, Date lastUpdated, List<Status> statuses) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = getStatuses(statuses);
    }

    private List<StatusResponse> getStatuses(List<Status> statuses) {
        if (CollectionUtils.isEmpty(statuses)) {
            return null;
        } else {
            List<StatusResponse> result = new ArrayList<>();
            for (Status status : statuses) {
                result.add(new StatusResponse(status.getImageUrl(), status.getTimeStamp()));
            }
            return result;
        }
    }
}
