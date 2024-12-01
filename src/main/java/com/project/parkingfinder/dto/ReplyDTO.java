package com.project.parkingfinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ReplyDTO {

    @Getter
    @Setter
    public static class CreateReplyRequest {

        @NotNull(message = "User ID cannot be null")
        private Long userId;

        @NotNull(message = "Review ID cannot be null")
        private Long reviewId;

        @NotBlank(message = "Comment cannot be blank")
        private String comment;

        private String imageUrls;
    }
}
