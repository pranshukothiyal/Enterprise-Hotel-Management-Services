package com.icwd.booking.BookingService.repository;

import com.icwd.booking.BookingService.entitites.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<BookingEntity, String> {

    List<BookingEntity> findByUserId(String userId);

    List<BookingEntity> findByHotelId(String hotelId);

    List<BookingEntity> findByBookingStatus(String bookingStatus);
}