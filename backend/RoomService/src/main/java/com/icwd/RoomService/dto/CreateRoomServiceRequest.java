package com.icwd.RoomService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRoomServiceRequest(

        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Booking ID is required")
        String bookingId,

        @NotBlank(message = "Room ID is required")
        String roomId,

        @NotBlank(message = "Service ID is required")
        String serviceId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,

        String specialInstructions
) {
}