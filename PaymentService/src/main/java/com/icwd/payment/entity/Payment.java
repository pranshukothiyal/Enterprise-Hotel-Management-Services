package com.icwd.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_booking_id",
                        columnList = "booking_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_razorpay_order_id",
                        columnNames = "razorpay_order_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private String paymentId;

    @Column(
            name = "booking_id",
            nullable = false
    )
    private String bookingId;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    @Builder.Default
    @Column(
            nullable = false,
            length = 3
    )
    private String currency = "INR";

    @Column(
            nullable = false,
            unique = true,
            length = 40
    )
    private String receipt;

    @Column(
            name = "razorpay_order_id",
            nullable = false,
            unique = true
    )
    private String razorpayOrderId;

    @Column(
            name = "razorpay_payment_id",
            unique = true
    )
    private String razorpayPaymentId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_mode",
            nullable = false
    )
    private PaymentMode paymentMode =
            PaymentMode.RAZORPAY;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false
    )
    private PaymentStatus paymentStatus =
            PaymentStatus.CREATED;

    @Column(
            name = "failure_reason",
            length = 500
    )
    private String failureReason;

    /*
     * These are kept for compatibility with your
     * existing code. For Razorpay, razorpayPaymentId
     * is the actual external transaction ID.
     */
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void setDefaultValues() {

        if (currency == null
                || currency.isBlank()) {
            currency = "INR";
        }

        if (paymentMode == null) {
            paymentMode =
                    PaymentMode.RAZORPAY;
        }

        if (paymentStatus == null) {
            paymentStatus =
                    PaymentStatus.CREATED;
        }
    }
}