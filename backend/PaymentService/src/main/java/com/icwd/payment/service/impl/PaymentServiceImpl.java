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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @Override
    @Transactional
    public CreateOrderResponse createRazorpayOrder(
            CreateOrderRequest request
    ) {

        log.info(
                "Starting Razorpay order creation. bookingId={}",
                request.bookingId()
        );

        BookingPaymentDetails booking =
                getBookingDetails(
                        request.bookingId()
                );

        log.debug(
                "Booking details received for payment creation. bookingId={}, status={}",
                booking.bookingId(),
                booking.bookingStatus()
        );

        validateBooking(
                booking
        );

        log.debug(
                "Booking validation completed successfully. bookingId={}",
                booking.bookingId()
        );

        boolean alreadyPaid =
                paymentRepository
                        .existsByBookingIdAndPaymentStatus(
                                booking.bookingId(),
                                PaymentStatus.CAPTURED
                        );

        if (alreadyPaid) {

            log.warn(
                    "Payment order creation rejected because booking is already paid. bookingId={}",
                    booking.bookingId()
            );

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This booking is already paid"
            );
        }

        long amountInPaise =
                convertRupeesToPaise(
                        booking.totalAmount()
                );

        log.debug(
                "Converted booking amount to paise. bookingId={}, amountInPaise={}",
                booking.bookingId(),
                amountInPaise
        );

        String receipt =
                createReceipt(
                        booking.bookingId()
                );

        log.debug(
                "Payment receipt generated. bookingId={}, receipt={}",
                booking.bookingId(),
                receipt
        );

        try {

            log.info(
                    "Creating order with Razorpay. bookingId={}",
                    booking.bookingId()
            );

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

            JSONObject notes =
                    new JSONObject();

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
                            .create(
                                    orderRequest
                            );

            String razorpayOrderId =
                    razorpayOrder.get("id");

            log.info(
                    "Razorpay order created successfully. bookingId={}, razorpayOrderId={}",
                    booking.bookingId(),
                    razorpayOrderId
            );

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
                    paymentRepository.save(
                            payment
                    );

            log.info(
                    "Payment record created successfully. paymentId={}, bookingId={}, razorpayOrderId={}, status={}",
                    savedPayment.getPaymentId(),
                    savedPayment.getBookingId(),
                    savedPayment.getRazorpayOrderId(),
                    savedPayment.getPaymentStatus()
            );

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

            log.error(
                    "Failed to create Razorpay order. bookingId={}",
                    booking.bookingId(),
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to create Razorpay order",
                    exception
            );
        }
    }

    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    @Override
    @Transactional
    public VerifyPaymentResponse verifyRazorpayPayment(
            VerifyPaymentRequest request
    ) {

        log.info(
                "Starting Razorpay payment verification. razorpayOrderId={}",
                request.razorpayOrderId()
        );

        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.razorpayOrderId()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Payment verification failed because Razorpay order was not found. razorpayOrderId={}",
                                    request.razorpayOrderId()
                            );

                            return new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Razorpay order was not found"
                            );
                        });

        log.debug(
                "Payment record found for verification. paymentId={}, bookingId={}, status={}",
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getPaymentStatus()
        );

        if (payment.getPaymentStatus()
                == PaymentStatus.CAPTURED) {

            log.info(
                    "Payment is already captured. paymentId={}, bookingId={}",
                    payment.getPaymentId(),
                    payment.getBookingId()
            );

            return createVerificationResponse(
                    payment,
                    true,
                    "Payment was already captured"
            );
        }

        try {

            log.debug(
                    "Verifying Razorpay payment signature. paymentId={}, razorpayOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId()
            );

            JSONObject verificationData =
                    new JSONObject();

            verificationData.put(
                    "razorpay_order_id",
                    payment.getRazorpayOrderId()
            );

            verificationData.put(
                    "razorpay_payment_id",
                    request.razorpayPaymentId()
            );

            /*
             * Never log razorpaySignature.
             */
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

                log.warn(
                        "Razorpay payment signature verification failed. paymentId={}, bookingId={}, razorpayOrderId={}",
                        payment.getPaymentId(),
                        payment.getBookingId(),
                        payment.getRazorpayOrderId()
                );

                payment.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                payment.setFailureReason(
                        "Invalid Razorpay signature"
                );

                paymentRepository.save(
                        payment
                );

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid Razorpay payment signature"
                );
            }

            log.debug(
                    "Razorpay payment signature verified successfully. paymentId={}",
                    payment.getPaymentId()
            );

            log.info(
                    "Fetching payment details from Razorpay. paymentId={}, razorpayPaymentId={}",
                    payment.getPaymentId(),
                    request.razorpayPaymentId()
            );

            com.razorpay.Payment remotePayment =
                    razorpayClient
                            .payments
                            .fetch(
                                    request.razorpayPaymentId()
                            );

            log.debug(
                    "Payment details received from Razorpay. paymentId={}",
                    payment.getPaymentId()
            );

            verifyRazorpayPaymentDetails(
                    payment,
                    remotePayment
            );

            log.debug(
                    "Razorpay payment details validated successfully. paymentId={}",
                    payment.getPaymentId()
            );

            String remoteStatus =
                    remotePayment.get(
                            "status"
                    );

            payment.setRazorpayPaymentId(
                    request.razorpayPaymentId()
            );

            payment.setTransactionId(
                    request.razorpayPaymentId()
            );

            payment.setPaymentDate(
                    LocalDateTime.now()
            );

            payment.setFailureReason(
                    null
            );

            if ("captured".equalsIgnoreCase(
                    remoteStatus
            )) {

                payment.setPaymentStatus(
                        PaymentStatus.CAPTURED
                );

                log.info(
                        "Razorpay reports payment as captured. paymentId={}, bookingId={}",
                        payment.getPaymentId(),
                        payment.getBookingId()
                );

            } else if ("authorized".equalsIgnoreCase(
                    remoteStatus
            )) {

                payment.setPaymentStatus(
                        PaymentStatus.AUTHORIZED
                );

                log.info(
                        "Razorpay reports payment as authorized. paymentId={}, bookingId={}",
                        payment.getPaymentId(),
                        payment.getBookingId()
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

                log.warn(
                        "Razorpay reports payment as failed. paymentId={}, bookingId={}",
                        payment.getPaymentId(),
                        payment.getBookingId()
                );

            } else {

                payment.setPaymentStatus(
                        PaymentStatus.CREATED
                );

                log.debug(
                        "Razorpay returned non-final payment status. paymentId={}, remoteStatus={}",
                        payment.getPaymentId(),
                        remoteStatus
                );
            }

            Payment savedPayment =
                    paymentRepository.save(
                            payment
                    );

            log.info(
                    "Payment verification completed. paymentId={}, bookingId={}, status={}",
                    savedPayment.getPaymentId(),
                    savedPayment.getBookingId(),
                    savedPayment.getPaymentStatus()
            );

            String message =
                    switch (
                            savedPayment
                                    .getPaymentStatus()
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

            log.error(
                    "Razorpay communication failed during payment verification. paymentId={}, bookingId={}",
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to verify Razorpay payment",
                    exception
            );
        }
    }

    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            String paymentId
    ) {

        log.debug(
                "Fetching payment by ID. paymentId={}",
                paymentId
        );

        Payment payment =
                paymentRepository
                        .findById(
                                paymentId
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Payment not found. paymentId={}",
                                    paymentId
                            );

                            return new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Payment not found: "
                                            + paymentId
                            );
                        });

        log.debug(
                "Payment fetched successfully. paymentId={}, bookingId={}, status={}",
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getPaymentStatus()
        );

        return mapToResponse(
                payment
        );
    }

    // =========================================================
    // GET PAYMENT BY BOOKING
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            String bookingId
    ) {

        log.debug(
                "Fetching latest payment by booking. bookingId={}",
                bookingId
        );

        Payment payment =
                paymentRepository
                        .findTopByBookingIdOrderByCreatedAtDesc(
                                bookingId
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "No payment found for booking. bookingId={}",
                                    bookingId
                            );

                            return new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "No payment found for booking: "
                                            + bookingId
                            );
                        });

        log.debug(
                "Latest payment fetched successfully. bookingId={}, paymentId={}, status={}",
                bookingId,
                payment.getPaymentId(),
                payment.getPaymentStatus()
        );

        return mapToResponse(
                payment
        );
    }

    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        log.debug(
                "Fetching all payments from repository"
        );

        List<PaymentResponse> payments =
                paymentRepository
                        .findAll()
                        .stream()
                        .map(
                                this::mapToResponse
                        )
                        .toList();

        log.info(
                "Payments fetched successfully. count={}",
                payments.size()
        );

        return payments;
    }

    // =========================================================
    // RAZORPAY WEBHOOK
    // =========================================================

    @Override
    @Transactional
    public void processRazorpayWebhook(
            String rawPayload,
            String razorpaySignature
    ) {

        log.info(
                "Starting Razorpay webhook processing"
        );

        /*
         * Do NOT log:
         *
         * rawPayload
         * razorpaySignature
         * razorpayWebhookSecret
         */

        try {

            boolean validSignature =
                    Utils.verifyWebhookSignature(
                            rawPayload,
                            razorpaySignature,
                            razorpayWebhookSecret
                    );

            if (!validSignature) {

                log.warn(
                        "Razorpay webhook rejected because signature is invalid"
                );

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid webhook signature"
                );
            }

            log.debug(
                    "Razorpay webhook signature verified successfully"
            );

            JSONObject webhook =
                    new JSONObject(
                            rawPayload
                    );

            String event =
                    webhook.optString(
                            "event"
                    );

            log.info(
                    "Processing Razorpay webhook event. event={}",
                    event
            );

            if (!event.equals(
                    "payment.authorized"
            )
                    && !event.equals(
                    "payment.captured"
            )
                    && !event.equals(
                    "payment.failed"
            )) {

                log.debug(
                        "Ignoring unsupported Razorpay webhook event. event={}",
                        event
                );

                return;
            }

            JSONObject paymentEntity =
                    webhook
                            .getJSONObject(
                                    "payload"
                            )
                            .getJSONObject(
                                    "payment"
                            )
                            .getJSONObject(
                                    "entity"
                            );

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

                log.warn(
                        "Razorpay webhook ignored because order ID is missing. event={}",
                        event
                );

                return;
            }

            log.debug(
                    "Searching payment for Razorpay webhook. razorpayOrderId={}, event={}",
                    razorpayOrderId,
                    event
            );

            Payment payment =
                    paymentRepository
                            .findByRazorpayOrderId(
                                    razorpayOrderId
                            )
                            .orElse(
                                    null
                            );

            if (payment == null) {

                log.warn(
                        "Razorpay webhook payment was not found locally. razorpayOrderId={}, event={}",
                        razorpayOrderId,
                        event
                );

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

                    payment.setFailureReason(
                            null
                    );

                    log.info(
                            "Payment authorized through Razorpay webhook. paymentId={}, bookingId={}",
                            payment.getPaymentId(),
                            payment.getBookingId()
                    );
                }

                case "payment.captured" -> {

                    payment.setPaymentStatus(
                            PaymentStatus.CAPTURED
                    );

                    payment.setFailureReason(
                            null
                    );

                    log.info(
                            "Payment captured through Razorpay webhook. paymentId={}, bookingId={}",
                            payment.getPaymentId(),
                            payment.getBookingId()
                    );
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

                    log.warn(
                            "Payment failure received through Razorpay webhook. paymentId={}, bookingId={}",
                            payment.getPaymentId(),
                            payment.getBookingId()
                    );
                }

                default -> {

                    log.debug(
                            "Unhandled Razorpay webhook event. event={}",
                            event
                    );

                    return;
                }
            }

            paymentRepository.save(
                    payment
            );

            log.info(
                    "Razorpay webhook processed successfully. paymentId={}, bookingId={}, status={}",
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    payment.getPaymentStatus()
            );

        } catch (RazorpayException exception) {

            log.error(
                    "Razorpay webhook signature verification failed",
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Webhook verification failed",
                    exception
            );
        }
    }

    // =========================================================
    // BOOKING SERVICE COMMUNICATION
    // =========================================================

    private BookingPaymentDetails getBookingDetails(
            String bookingId
    ) {

        log.info(
                "Calling BOOKING-SERVICE for payment details. bookingId={}",
                bookingId
        );

        try {

            BookingPaymentDetails booking =
                    bookingServiceClient
                            .getPaymentDetails(
                                    bookingId
                            );

            log.debug(
                    "Payment details received successfully from BOOKING-SERVICE. bookingId={}",
                    bookingId
            );

            return booking;

        } catch (FeignException.NotFound exception) {

            log.warn(
                    "BOOKING-SERVICE reported booking not found. bookingId={}",
                    bookingId
            );

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found: "
                            + bookingId,
                    exception
            );

        } catch (FeignException exception) {

            log.error(
                    "Failed to communicate with BOOKING-SERVICE. bookingId={}, status={}",
                    bookingId,
                    exception.status(),
                    exception
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "BookingService is unavailable",
                    exception
            );
        }
    }

    // =========================================================
    // BOOKING VALIDATION
    // =========================================================

    private void validateBooking(
            BookingPaymentDetails booking
    ) {

        log.debug(
                "Validating booking before payment creation"
        );

        if (booking == null
                || booking.bookingId() == null) {

            log.warn(
                    "Payment creation rejected because booking information is missing"
            );

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found"
            );
        }

        if (booking.totalAmount() == null
                || booking.totalAmount()
                .compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            log.warn(
                    "Payment creation rejected because booking amount is invalid. bookingId={}",
                    booking.bookingId()
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking amount must be greater than zero"
            );
        }

        if ("CANCELLED".equalsIgnoreCase(
                booking.bookingStatus()
        )) {

            log.warn(
                    "Payment creation rejected because booking is cancelled. bookingId={}",
                    booking.bookingId()
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create payment for a cancelled booking"
            );
        }

        log.debug(
                "Booking validation successful. bookingId={}",
                booking.bookingId()
        );
    }

    // =========================================================
    // VERIFY PAYMENT DETAILS FROM RAZORPAY
    // =========================================================

    private void verifyRazorpayPaymentDetails(
            Payment payment,
            com.razorpay.Payment remotePayment
    ) {

        log.debug(
                "Validating payment details against Razorpay. paymentId={}",
                payment.getPaymentId()
        );

        String remoteOrderId =
                remotePayment.get(
                        "order_id"
                );

        String remoteCurrency =
                remotePayment.get(
                        "currency"
                );

        Number remoteAmountValue =
                remotePayment.get(
                        "amount"
                );

        if (!payment
                .getRazorpayOrderId()
                .equals(
                        remoteOrderId
                )) {

            log.warn(
                    "Razorpay order ID mismatch detected. paymentId={}, expectedOrderId={}, receivedOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId(),
                    remoteOrderId
            );

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

            log.warn(
                    "Razorpay payment amount mismatch. paymentId={}, expectedAmount={}, receivedAmount={}",
                    payment.getPaymentId(),
                    expectedAmount,
                    remoteAmountValue
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Razorpay amount mismatch"
            );
        }

        if (remoteCurrency == null
                || !payment
                .getCurrency()
                .equalsIgnoreCase(
                        remoteCurrency
                )) {

            log.warn(
                    "Razorpay payment currency mismatch. paymentId={}, expectedCurrency={}, receivedCurrency={}",
                    payment.getPaymentId(),
                    payment.getCurrency(),
                    remoteCurrency
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Razorpay currency mismatch"
            );
        }

        log.debug(
                "Razorpay payment details matched successfully. paymentId={}",
                payment.getPaymentId()
        );
    }

    // =========================================================
    // RUPEES -> PAISE
    // =========================================================

    private long convertRupeesToPaise(
            BigDecimal amount
    ) {

        log.trace(
                "Converting rupees to paise"
        );

        return amount
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .movePointRight(
                        2
                )
                .longValueExact();
    }

    // =========================================================
    // RECEIPT
    // =========================================================

    private String createReceipt(
            String bookingId
    ) {

        log.trace(
                "Generating payment receipt. bookingId={}",
                bookingId
        );

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

        String receipt =
                "BK_"
                        + cleanedBookingId
                        + "_"
                        + UUID.randomUUID()
                        .toString()
                        .substring(
                                0,
                                8
                        );

        log.trace(
                "Payment receipt generated. bookingId={}, receipt={}",
                bookingId,
                receipt
        );

        return receipt;
    }

    // =========================================================
    // VERIFICATION RESPONSE
    // =========================================================

    private VerifyPaymentResponse
    createVerificationResponse(
            Payment payment,
            boolean verified,
            String message
    ) {

        log.trace(
                "Creating payment verification response. paymentId={}, verified={}, status={}",
                payment.getPaymentId(),
                verified,
                payment.getPaymentStatus()
        );

        return new VerifyPaymentResponse(
                verified,
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getRazorpayPaymentId(),
                payment.getPaymentStatus().name(),
                message
        );
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

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