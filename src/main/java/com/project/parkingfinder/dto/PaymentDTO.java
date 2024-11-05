package com.project.parkingfinder.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.parkingfinder.enums.PaymentMethod;
import com.project.parkingfinder.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {

    @NotNull(message = "Reservation ID cannot be null")
    @JsonProperty("reservation_id")
    private Long reservationId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private Double amount;

    @NotNull(message = "Payment method cannot be null")
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("payment_status")
    private PaymentStatus paymentStatus;
}
