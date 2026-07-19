package com.icwd.hotel.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hotel_room_number",
                        columnNames = {"hotel_id", "room_number"}
                )
        }
)
public class Room {

    @Id
    @Column(name = "room_id")
    private String roomId;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Room price must be greater than zero"
    )
    @Column(
            name = "price_per_night",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal pricePerNight;

    @Positive(message = "Room capacity must be greater than zero")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_status", nullable = false)
    private RoomStatus roomStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hotel hotel;
}