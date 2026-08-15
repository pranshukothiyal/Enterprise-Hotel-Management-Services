package com.icwd.booking.BookingService.services;

import com.icwd.booking.BookingService.dto.BookingPaymentDetails;
import com.icwd.booking.BookingService.entitites.BookingEntity;

import java.time.LocalDate;
import java.util.List;

public interface Service {

    BookingEntity createBooking(BookingEntity bookingEntity);

    List<BookingEntity> getAllBookings();

    BookingEntity getBooking(String bookingId);

    List<BookingEntity> getBookingsByUser(String userId);

    List<BookingEntity> getBookingsByHotel(String hotelId);

    BookingEntity updateBooking(String bookingId, BookingEntity booking);

    void deleteBooking(String bookingId);

    BookingPaymentDetails getPaymentDetails(
            String bookingId
    );

    boolean isRoomAvailable(
            String roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}
