package com.icwd.payment.dto;

public record VerifyPaymentResponse(

        boolean verified,

        String paymentId,

        String bookingId,

        String razorpayPaymentId,

        String status,

        String message

) {
}