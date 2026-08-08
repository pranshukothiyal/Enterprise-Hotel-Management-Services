package com.icwd.payment.controller;

import com.icwd.payment.dto.*;
import com.icwd.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/razorpay/orders")
    public ResponseEntity<CreateOrderResponse>
    createRazorpayOrder(
            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        paymentService
                                .createRazorpayOrder(request)
                );
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<VerifyPaymentResponse>
    verifyRazorpayPayment(
            @Valid
            @RequestBody
            VerifyPaymentRequest request
    ) {
        return ResponseEntity.ok(
                paymentService
                        .verifyRazorpayPayment(request)
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse>
    getPaymentById(
            @PathVariable String paymentId
    ) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentById(paymentId)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse>
    getPaymentByBookingId(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(
                paymentService
                        .getPaymentByBookingId(bookingId)
        );
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>>
    getAllPayments() {
        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @PostMapping("/razorpay/webhook")
    public ResponseEntity<Void>
    processRazorpayWebhook(
            @RequestBody String rawPayload,

            @RequestHeader(
                    "X-Razorpay-Signature"
            )
            String razorpaySignature
    ) {
        paymentService.processRazorpayWebhook(
                rawPayload,
                razorpaySignature
        );

        return ResponseEntity.ok().build();
    }
}