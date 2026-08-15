package com.icwd.invoice.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookingDto {
    private String bookingId;
    private String userId;
    private String hotelId;
    private String roomId;
    private Double totalAmount;
    private String status;
}
