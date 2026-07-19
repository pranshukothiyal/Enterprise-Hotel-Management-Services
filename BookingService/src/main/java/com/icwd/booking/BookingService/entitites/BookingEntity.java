package com.icwd.booking.BookingService.entitites;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {

    @Id
    private String bookingId;

    private String userId;

    private String hotelId;

    private String checkInDate;

    private String checkOutDate;

    private Double totalAmount;

    private String bookingStatus;
}