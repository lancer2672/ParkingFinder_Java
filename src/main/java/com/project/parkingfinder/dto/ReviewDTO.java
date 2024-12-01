package com.project.parkingfinder.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ReviewDTO {

    @Getter
    @Setter
    public static class CreateReviewRequest {

        @NotNull(message = "User ID cannot be null")
        private Long userId;

        @NotNull(message = "Parking Lot ID cannot be null")
        private Long parkingLotId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must not exceed 5")
        private Integer rating;

        @NotBlank(message = "Comment cannot be blank")
        private String comment;

        private String imageId; // Optional, so no validation annotations
    }
}
