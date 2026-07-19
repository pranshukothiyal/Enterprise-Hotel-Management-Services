package com.icwd.booking.BookingService.services.impl;

import com.icwd.booking.BookingService.entitites.BookingEntity;
import com.icwd.booking.BookingService.exceptions.ResourceNotFoundException;
import com.icwd.booking.BookingService.repository.BookingRepository;
import com.icwd.booking.BookingService.services.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class BookingServiceImpl implements Service {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingEntity createBooking(BookingEntity bookingEntity) {

        validateBookingDates(
                bookingEntity.getCheckInDate(),
                bookingEntity.getCheckOutDate()
        );

        bookingEntity.setBookingId(UUID.randomUUID().toString());

        if (bookingEntity.getBookingStatus() == null ||
                bookingEntity.getBookingStatus().isBlank()) {

            bookingEntity.setBookingStatus("PENDING");
        }

        return bookingRepository.save(bookingEntity);
    }

    @Override
    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public BookingEntity getBooking(String bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with ID: " + bookingId
                        )
                );
    }

    @Override
    public List<BookingEntity> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<BookingEntity> getBookingsByHotel(String hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }

    @Override
    public BookingEntity updateBooking(
            String bookingId,
            BookingEntity booking
    ) {

        BookingEntity existingBooking = getBooking(bookingId);

        validateBookingDates(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        existingBooking.setUserId(booking.getUserId());
        existingBooking.setHotelId(booking.getHotelId());
        existingBooking.setCheckInDate(booking.getCheckInDate());
        existingBooking.setCheckOutDate(booking.getCheckOutDate());
        existingBooking.setTotalAmount(booking.getTotalAmount());
        existingBooking.setBookingStatus(booking.getBookingStatus());

        return bookingRepository.save(existingBooking);
    }

    @Override
    public void deleteBooking(String bookingId) {

        BookingEntity bookingEntity = getBooking(bookingId);

        bookingRepository.delete(bookingEntity);
    }

    private void validateBookingDates(
            String checkInDate,
            String checkOutDate
    ) {

        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required"
            );
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            if (!checkOut.isAfter(checkIn)) {
                throw new IllegalArgumentException(
                        "Check-out date must be after check-in date"
                );
            }

        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Date format must be yyyy-MM-dd"
            );
        }
    }
}