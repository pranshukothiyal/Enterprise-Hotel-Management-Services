package com.icwd.RoomService.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_service_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomServiceRequest {

    @Id
    @Column(name = "request_id", nullable = false)
    private String requestId;

    /*
     * These IDs belong to other microservices.
     * Do not use @ManyToOne for them.
     */

    @NotBlank(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotBlank(message = "Booking ID is required")
    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @NotBlank(message = "Room ID is required")
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "assigned_employee_id")
    private String assignedEmployeeId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(
            name = "special_instructions",
            columnDefinition = "TEXT"
    )
    private String specialInstructions;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    @Builder.Default
    private RequestStatus requestStatus =
            RequestStatus.PENDING;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;


    @NotNull(message = "Service offering is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private HotelServiceOffering serviceOffering;
}