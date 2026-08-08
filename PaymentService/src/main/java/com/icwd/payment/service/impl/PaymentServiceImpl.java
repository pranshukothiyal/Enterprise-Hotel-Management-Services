package com.icwd.payment.service.impl;

import com.icwd.payment.client.BookingService;
import com.icwd.payment.dto.*;
import com.icwd.payment.entity.Payment;
import com.icwd.payment.entity.PaymentMode;
import com.icwd.payment.entity.PaymentStatus;
import com.icwd.payment.repository.PaymentRepository;
import com.icwd.payment.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingService
            bookingServiceClient;

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook-secret}")
    private String razorpayWebhookSecret;

    @Override
    @Transactional
    public CreateOrderResponse createRazorpayOrder(
            CreateOrderRequest request
    ) {
        BookingPaymentDetails booking =
                getBookingDetails(
                        request.bookingId()
                );

        validateBooking(booking);

        boolean alreadyPaid =
                paymentRepository
                        .existsByBookingIdAndPaymentStatus(
                                booking.bookingId(),
                                PaymentStatus.CAPTURED
                        );

        if (alreadyPaid) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This booking is already paid"
            );
        }

        long amountInPaise =
                convertRupeesToPaise(
                        booking.totalAmount()
                );

        String receipt =
                createReceipt(
                        booking.bookingId()
                );

        try {
            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            orderRequest.put(
                    "receipt",
                    receipt
            );

            JSONObject notes = new JSONObject();

            notes.put(
                    "bookingId",
                    booking.bookingId()
            );

            orderRequest.put(
                    "notes",
                    notes
            );

            Order razorpayOrder =
                    razorpayClient
                            .orders
                            .create(orderRequest);

            String razorpayOrderId =
                    razorpayOrder.get("id");

            Payment payment =
                    Payment.builder()
                            .bookingId(
                                    booking.bookingId()
                            )
                            .amount(
                                    booking.totalAmount()
                            )
                            .currency("INR")
                            .receipt(receipt)
                            .razorpayOrderId(
                                    razorpayOrderId
                            )
                            .paymentMode(
                                    PaymentMode.RAZORPAY
                            )
                            .paymentStatus(
                                    PaymentStatus.CREATED
                            )
                            .build();

            Payment savedPayment =
                    paymentRepository.save(payment);

            return new CreateOrderResponse(
                    savedPayment.getPaymentId(),
                    savedPayment.getBookingId(),
                    razorpayKeyId,
                    savedPayment.getRazorpayOrderId(),
                    amountInPaise,
                    savedPayment.getCurrency(),
                    savedPayment
                            .getPaymentStatus()
                            .name()
            );

        } catch (RazorpayException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to create Razorpay order",
                    exception
            );
        }
    }

    @Override
    @Transactional
    public VerifyPaymentResponse verifyRazorpayPayment(
            VerifyPaymentRequest request
    ) {
        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.razorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Razorpay order was not found"
                                )
                        );

        if (payment.getPaymentStatus()
                == PaymentStatus.CAPTURED) {

            return createVerificationResponse(
                    payment,
                    true,
                    "Payment was already captured"
            );
        }

        try {
            JSONObject verificationData =
                    new JSONObject();

            /*
             * Use the order ID saved in the database.
             */
            verificationData.put(
                    "razorpay_order_id",
                    payment.getRazorpayOrderId()
            );

            verificationData.put(
                    "razorpay_payment_id",
                    request.razorpayPaymentId()
            );

            verificationData.put(
                    "razorpay_signature",
                    request.razorpaySignature()
            );

            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            verificationData,
                            razorpayKeySecret
                    );

            if (!signatureValid) {
                payment.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                payment.setFailureReason(
                        "Invalid Razorpay signature"
                );

                paymentRepository.save(payment);

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid Razorpay payment signature"
                );
            }

            com.razorpay.Payment remotePayment =
                    razorpayClient
                            .payments
                            .fetch(
                                    request.razorpayPaymentId()
                            );

            verifyRazorpayPaymentDetails(
                    payment,
                    remotePayment
            );

            String remoteStatus =
                    remotePayment.get("status");

            payment.setRazorpayPaymentId(
                    request.razorpayPaymentId()
            );

            payment.setTransactionId(
                    request.razorpayPaymentId()
            );

            payment.setPaymentDate(
                    LocalDateTime.now()
            );

            payment.setFailureReason(null);

            if ("captured".equalsIgnoreCase(
                    remoteStatus
            )) {
                payment.setPaymentStatus(
                        PaymentStatus.CAPTURED
                );

            } else if ("authorized".equalsIgnoreCase(
                    remoteStatus
            )) {
                payment.setPaymentStatus(
                        PaymentStatus.AUTHORIZED
                );

            } else if ("failed".equalsIgnoreCase(
                    remoteStatus
            )) {
                payment.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                payment.setFailureReason(
                        "Razorpay reported payment failure"
                );

            } else {
                payment.setPaymentStatus(
                        PaymentStatus.CREATED
                );
            }

            Payment savedPayment =
                    paymentRepository.save(payment);

            String message =
                    switch (
                            savedPayment.getPaymentStatus()
                            ) {
                        case CAPTURED ->
                                "Payment captured successfully";

                        case AUTHORIZED ->
                                "Payment authorized; capture is pending";

                        case FAILED ->
                                "Payment failed";

                        default ->
                                "Payment verification completed";
                    };

            return createVerificationResponse(
                    savedPayment,
                    savedPayment.getPaymentStatus()
                            != PaymentStatus.FAILED,
                    message
            );

        } catch (RazorpayException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to verify Razorpay payment",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            String paymentId
    ) {
        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Payment not found: "
                                                + paymentId
                                )
                        );

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            String bookingId
    ) {
        Payment payment =
                paymentRepository
                        .findTopByBookingIdOrderByCreatedAtDesc(
                                bookingId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No payment found for booking: "
                                                + bookingId
                                )
                        );

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void processRazorpayWebhook(
            String rawPayload,
            String razorpaySignature
    ) {
        try {
            boolean validSignature =
                    Utils.verifyWebhookSignature(
                            rawPayload,
                            razorpaySignature,
                            razorpayWebhookSecret
                    );

            if (!validSignature) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid webhook signature"
                );
            }

            JSONObject webhook =
                    new JSONObject(rawPayload);

            String event =
                    webhook.optString("event");

            if (!event.equals("payment.authorized")
                    && !event.equals("payment.captured")
                    && !event.equals("payment.failed")) {
                return;
            }

            JSONObject paymentEntity =
                    webhook
                            .getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

            String razorpayOrderId =
                    paymentEntity.optString(
                            "order_id",
                            null
                    );

            String razorpayPaymentId =
                    paymentEntity.optString(
                            "id",
                            null
                    );

            if (razorpayOrderId == null
                    || razorpayOrderId.isBlank()) {
                return;
            }

            Payment payment =
                    paymentRepository
                            .findByRazorpayOrderId(
                                    razorpayOrderId
                            )
                            .orElse(null);

            if (payment == null) {
                return;
            }

            payment.setRazorpayPaymentId(
                    razorpayPaymentId
            );

            payment.setTransactionId(
                    razorpayPaymentId
            );

            payment.setPaymentDate(
                    LocalDateTime.now()
            );

            switch (event) {

                case "payment.authorized" -> {
                    payment.setPaymentStatus(
                            PaymentStatus.AUTHORIZED
                    );

                    payment.setFailureReason(null);
                }

                case "payment.captured" -> {
                    payment.setPaymentStatus(
                            PaymentStatus.CAPTURED
                    );

                    payment.setFailureReason(null);
                }

                case "payment.failed" -> {
                    payment.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    payment.setFailureReason(
                            paymentEntity.optString(
                                    "error_description",
                                    "Payment failed"
                            )
                    );
                }

                default -> {
                    return;
                }
            }

            paymentRepository.save(payment);

        } catch (RazorpayException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Webhook verification failed",
                    exception
            );
        }
    }

    private BookingPaymentDetails getBookingDetails(
            String bookingId
    ) {
        try {
            return bookingServiceClient
                    .getPaymentDetails(bookingId);

        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found: " + bookingId,
                    exception
            );

        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "BookingService is unavailable",
                    exception
            );
        }
    }

    private void validateBooking(
            BookingPaymentDetails booking
    ) {
        if (booking == null
                || booking.bookingId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found"
            );
        }

        if (booking.totalAmount() == null
                || booking.totalAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking amount must be greater than zero"
            );
        }

        if ("CANCELLED".equalsIgnoreCase(
                booking.bookingStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create payment for a cancelled booking"
            );
        }
    }

    private void verifyRazorpayPaymentDetails(
            Payment payment,
            com.razorpay.Payment remotePayment
    ) {
        String remoteOrderId =
                remotePayment.get("order_id");

        String remoteCurrency =
                remotePayment.get("currency");

        Number remoteAmountValue =
                remotePayment.get("amount");

        if (!payment.getRazorpayOrderId()
                .equals(remoteOrderId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Razorpay order ID mismatch"
            );
        }

        long expectedAmount =
                convertRupeesToPaise(
                        payment.getAmount()
                );

        if (remoteAmountValue == null
                || remoteAmountValue.longValue()
                != expectedAmount) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Razorpay amount mismatch"
            );
        }

        if (remoteCurrency == null
                || !payment.getCurrency()
                .equalsIgnoreCase(remoteCurrency)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Razorpay currency mismatch"
            );
        }
    }

    private long convertRupeesToPaise(
            BigDecimal amount
    ) {
        return amount
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .movePointRight(2)
                .longValueExact();
    }

    private String createReceipt(
            String bookingId
    ) {
        String cleanedBookingId =
                bookingId.replaceAll(
                        "[^A-Za-z0-9_-]",
                        ""
                );

        if (cleanedBookingId.length() > 18) {
            cleanedBookingId =
                    cleanedBookingId.substring(
                            0,
                            18
                    );
        }

        return "BK_"
                + cleanedBookingId
                + "_"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }

    private VerifyPaymentResponse
    createVerificationResponse(
            Payment payment,
            boolean verified,
            String message
    ) {
        return new VerifyPaymentResponse(
                verified,
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getRazorpayPaymentId(),
                payment.getPaymentStatus().name(),
                message
        );
    }

    private PaymentResponse mapToResponse(
            Payment payment
    ) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getPaymentMode().name(),
                payment.getPaymentStatus().name(),
                payment.getFailureReason(),
                payment.getPaymentDate(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}