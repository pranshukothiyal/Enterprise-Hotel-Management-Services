package com.icwd.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        String paymentId,

        String bookingId,

        BigDecimal amount,

        String currency,

        String razorpayOrderId,

        String razorpayPaymentId,

        String paymentMode,

        String paymentStatus,

        String failureReason,

        LocalDateTime paymentDate,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}