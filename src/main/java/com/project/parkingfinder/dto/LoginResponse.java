package com.project.parkingfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String role;
    private String accessToken;
    private String refreshToken;
}