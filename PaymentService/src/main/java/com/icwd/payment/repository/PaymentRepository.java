package com.icwd.payment.repository;

import com.icwd.payment.entity.Payment;
import com.icwd.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, String> {

    Optional<Payment> findByRazorpayOrderId(
            String razorpayOrderId
    );

    Optional<Payment>
    findTopByBookingIdOrderByCreatedAtDesc(
            String bookingId
    );

    boolean existsByBookingIdAndPaymentStatus(
            String bookingId,
            PaymentStatus paymentStatus
    );
}