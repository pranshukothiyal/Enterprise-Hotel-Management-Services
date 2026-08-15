package com.icwd.payment.controller;

import com.icwd.payment.dto.*;
import com.icwd.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info(
                "Received request to create Razorpay payment order"
        );

        CreateOrderResponse response =
                paymentService
                        .createRazorpayOrder(request);

        log.info(
                "Razorpay payment order created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<VerifyPaymentResponse>
    verifyRazorpayPayment(
            @Valid
            @RequestBody
            VerifyPaymentRequest request
    ) {

        log.info(
                "Received request to verify Razorpay payment"
        );

        VerifyPaymentResponse response =
                paymentService
                        .verifyRazorpayPayment(request);

        log.info(
                "Razorpay payment verification completed successfully"
        );

        return ResponseEntity.ok(
                response
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse>
    getPaymentById(
            @PathVariable String paymentId
    ) {

        log.info(
                "Received request to fetch payment. paymentId={}",
                paymentId
        );

        PaymentResponse payment =
                paymentService
                        .getPaymentById(paymentId);

        log.debug(
                "Payment fetched successfully. paymentId={}",
                paymentId
        );

        return ResponseEntity.ok(
                payment
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse>
    getPaymentByBookingId(
            @PathVariable String bookingId
    ) {

        log.info(
                "Received request to fetch payment by booking. bookingId={}",
                bookingId
        );

        PaymentResponse payment =
                paymentService
                        .getPaymentByBookingId(
                                bookingId
                        );

        log.debug(
                "Payment fetched successfully for booking. bookingId={}",
                bookingId
        );

        return ResponseEntity.ok(
                payment
        );
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>>
    getAllPayments() {

        log.info(
                "Received request to fetch all payments"
        );

        List<PaymentResponse> payments =
                paymentService
                        .getAllPayments();

        log.debug(
                "Fetched all payments successfully. count={}",
                payments.size()
        );

        return ResponseEntity.ok(
                payments
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

        log.info(
                "Received Razorpay webhook"
        );

        paymentService
                .processRazorpayWebhook(
                        rawPayload,
                        razorpaySignature
                );

        log.info(
                "Razorpay webhook processed successfully"
        );

        return ResponseEntity
                .ok()
                .build();
    }
}