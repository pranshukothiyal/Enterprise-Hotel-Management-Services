package com.icwd.booking.BookingService.dto;

import java.math.BigDecimal;

public record BookingPaymentDetails(

        String bookingId,

        BigDecimal totalAmount,

        String bookingStatus

) {
}