package com.icwd.RoomService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hotel_service_offerings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hotel_service",
                        columnNames = {
                                "hotel_id",
                                "service_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelServiceOffering {

    @Id
    @Column(name = "service_id", nullable = false)
    private String serviceId;

    @NotBlank(message = "Hotel ID is required")
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @NotBlank(message = "Service name is required")
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.0",
            message = "Price cannot be negative"
    )
    @Column(
            name = "price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_status", nullable = false)
    @Builder.Default
    private ServiceStatus serviceStatus =
            ServiceStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}