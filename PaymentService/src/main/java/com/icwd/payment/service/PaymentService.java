package com.icwd.payment.service;

import com.icwd.payment.dto.*;

import java.util.List;

public interface PaymentService {

    CreateOrderResponse createRazorpayOrder(
            CreateOrderRequest request
    );

    VerifyPaymentResponse verifyRazorpayPayment(
            VerifyPaymentRequest request
    );

    PaymentResponse getPaymentById(
            String paymentId
    );

    PaymentResponse getPaymentByBookingId(
            String bookingId
    );

    List<PaymentResponse> getAllPayments();

    void processRazorpayWebhook(
            String rawPayload,
            String razorpaySignature
    );
}