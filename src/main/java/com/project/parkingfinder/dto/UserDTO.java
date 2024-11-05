package com.project.parkingfinder.dto;

import com.project.parkingfinder.enums.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private UserStatus status;
    private String apiKey;
}
