package com.icwd.payment.dto;

public record CreateOrderResponse(

        String paymentId,

        String bookingId,

        String razorpayKeyId,

        String razorpayOrderId,

        long amount,

        String currency,

        String status

) {
}