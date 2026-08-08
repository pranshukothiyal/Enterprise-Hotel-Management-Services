package com.icwd.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(

        @NotBlank(message = "Booking ID is required")
        String bookingId

) {
}