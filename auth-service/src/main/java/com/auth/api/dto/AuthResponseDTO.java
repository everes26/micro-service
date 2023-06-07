package com.auth.api.dto;

import com.auth.api.models.Users;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer ";
    private Long id;
    private String username;
    private String email;

    public AuthResponseDTO(String accessToken, Users user) {
        this.accessToken = accessToken;
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
