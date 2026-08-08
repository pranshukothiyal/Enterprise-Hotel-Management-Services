package com.icwd.booking.BookingService.services.impl;

import com.icwd.booking.BookingService.dto.BookingPaymentDetails;
import com.icwd.booking.BookingService.entitites.BookingEntity;
import com.icwd.booking.BookingService.exceptions.ResourceNotFoundException;
import com.icwd.booking.BookingService.repository.BookingRepository;
import com.icwd.booking.BookingService.services.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class BookingServiceImpl implements Service {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository
    ) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingEntity createBooking(
            BookingEntity bookingEntity
    ) {
        validateBookingDates(
                bookingEntity.getCheckInDate(),
                bookingEntity.getCheckOutDate()
        );

        bookingEntity.setBookingId(
                UUID.randomUUID().toString()
        );

        if (bookingEntity.getBookingStatus() == null
                || bookingEntity.getBookingStatus().isBlank()) {

            bookingEntity.setBookingStatus("PENDING");
        }

        return bookingRepository.save(
                bookingEntity
        );
    }

    @Override
    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public BookingEntity getBooking(
            String bookingId
    ) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with ID: "
                                        + bookingId
                        )
                );
    }

    @Override
    public List<BookingEntity> getBookingsByUser(
            String userId
    ) {
        return bookingRepository.findByUserId(
                userId
        );
    }

    @Override
    public List<BookingEntity> getBookingsByHotel(
            String hotelId
    ) {
        return bookingRepository.findByHotelId(
                hotelId
        );
    }

    @Override
    public BookingEntity updateBooking(
            String bookingId,
            BookingEntity booking
    ) {
        BookingEntity existingBooking =
                getBooking(bookingId);

        validateBookingDates(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        existingBooking.setUserId(
                booking.getUserId()
        );

        existingBooking.setHotelId(
                booking.getHotelId()
        );

        existingBooking.setCheckInDate(
                booking.getCheckInDate()
        );

        existingBooking.setCheckOutDate(
                booking.getCheckOutDate()
        );

        existingBooking.setTotalAmount(
                booking.getTotalAmount()
        );

        existingBooking.setBookingStatus(
                booking.getBookingStatus()
        );

        return bookingRepository.save(
                existingBooking
        );
    }

    @Override
    public void deleteBooking(
            String bookingId
    ) {
        BookingEntity bookingEntity =
                getBooking(bookingId);

        bookingRepository.delete(
                bookingEntity
        );
    }

    @Override
    public BookingPaymentDetails getPaymentDetails(
            String bookingId
    ) {
        BookingEntity booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found with ID: "
                                                + bookingId
                                )
                        );

        BigDecimal amount =
                convertToBigDecimal(
                        booking.getTotalAmount()
                );

        return new BookingPaymentDetails(
                booking.getBookingId(),
                amount,
                booking.getBookingStatus()
        );
    }

    private BigDecimal convertToBigDecimal(
            Object totalAmount
    ) {
        if (totalAmount == null) {
            throw new IllegalArgumentException(
                    "Booking amount is required"
            );
        }

        if (totalAmount instanceof BigDecimal amount) {
            return amount;
        }

        if (totalAmount instanceof Number number) {
            return BigDecimal.valueOf(
                    number.doubleValue()
            );
        }

        try {
            return new BigDecimal(
                    totalAmount.toString()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid booking amount"
            );
        }
    }

    private void validateBookingDates(
            String checkInDate,
            String checkOutDate
    ) {
        if (checkInDate == null
                || checkOutDate == null) {

            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required"
            );
        }

        try {
            LocalDate checkIn =
                    LocalDate.parse(checkInDate);

            LocalDate checkOut =
                    LocalDate.parse(checkOutDate);

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
    @Override
    public boolean isRoomAvailable(
            String roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException(
                    "Room ID is required"
            );
        }

        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required"
            );
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date"
            );
        }

        List<BookingEntity> bookings =
                bookingRepository.findByRoomId(roomId);

        for (BookingEntity booking : bookings) {

            if ("CANCELLED".equalsIgnoreCase(
                    booking.getBookingStatus()
            )) {
                continue;
            }

            LocalDate existingCheckIn =
                    LocalDate.parse(
                            booking.getCheckInDate()
                    );

            LocalDate existingCheckOut =
                    LocalDate.parse(
                            booking.getCheckOutDate()
                    );

            /*
             * Requested:
             * checkInDate -------- checkOutDate
             *
             * Existing:
             * existingCheckIn --- existingCheckOut
             *
             * They overlap when:
             *
             * requestedCheckIn < existingCheckOut
             * AND
             * requestedCheckOut > existingCheckIn
             */

            boolean overlap =
                    checkInDate.isBefore(existingCheckOut)
                            &&
                            checkOutDate.isAfter(existingCheckIn);

            if (overlap) {
                return false;
            }
        }

        return true;
    }

}