package com.icwd.booking.BookingService.services.impl;

import com.icwd.booking.BookingService.dto.BookingPaymentDetails;
import com.icwd.booking.BookingService.entitites.BookingEntity;
import com.icwd.booking.BookingService.exceptions.ResourceNotFoundException;
import com.icwd.booking.BookingService.repository.BookingRepository;
import com.icwd.booking.BookingService.services.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Slf4j
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

        log.info(
                "Starting booking creation. userId={}, hotelId={}, roomId={}",
                bookingEntity.getUserId(),
                bookingEntity.getHotelId(),
                bookingEntity.getRoomId()
        );

        log.debug(
                "Validating booking dates. checkInDate={}, checkOutDate={}",
                bookingEntity.getCheckInDate(),
                bookingEntity.getCheckOutDate()
        );

        validateBookingDates(
                bookingEntity.getCheckInDate(),
                bookingEntity.getCheckOutDate()
        );

        String bookingId =
                UUID.randomUUID().toString();

        bookingEntity.setBookingId(
                bookingId
        );

        log.debug(
                "Generated bookingId={}",
                bookingId
        );

        if (bookingEntity.getBookingStatus() == null
                || bookingEntity.getBookingStatus().isBlank()) {

            bookingEntity.setBookingStatus("PENDING");

            log.debug(
                    "Booking status was empty. Default status set to PENDING. bookingId={}",
                    bookingId
            );
        }

        BookingEntity savedBooking =
                bookingRepository.save(
                        bookingEntity
                );

        log.info(
                "Booking created successfully. bookingId={}, status={}",
                savedBooking.getBookingId(),
                savedBooking.getBookingStatus()
        );

        return savedBooking;
    }

    @Override
    public List<BookingEntity> getAllBookings() {

        log.debug("Fetching all bookings from database");

        List<BookingEntity> bookings =
                bookingRepository.findAll();

        log.info(
                "Fetched all bookings successfully. count={}",
                bookings.size()
        );

        return bookings;
    }

    @Override
    public BookingEntity getBooking(
            String bookingId
    ) {

        log.debug(
                "Fetching booking. bookingId={}",
                bookingId
        );

        return bookingRepository
                .findById(bookingId)
                .map(booking -> {

                    log.debug(
                            "Booking found. bookingId={}, status={}",
                            bookingId,
                            booking.getBookingStatus()
                    );

                    return booking;
                })
                .orElseThrow(() -> {

                    log.warn(
                            "Booking not found. bookingId={}",
                            bookingId
                    );

                    return new ResourceNotFoundException(
                            "Booking not found with ID: "
                                    + bookingId
                    );
                });
    }

    @Override
    public List<BookingEntity> getBookingsByUser(
            String userId
    ) {

        log.debug(
                "Fetching bookings for userId={}",
                userId
        );

        List<BookingEntity> bookings =
                bookingRepository.findByUserId(
                        userId
                );

        log.info(
                "Fetched bookings for user. userId={}, count={}",
                userId,
                bookings.size()
        );

        return bookings;
    }

    @Override
    public List<BookingEntity> getBookingsByHotel(
            String hotelId
    ) {

        log.debug(
                "Fetching bookings for hotelId={}",
                hotelId
        );

        List<BookingEntity> bookings =
                bookingRepository.findByHotelId(
                        hotelId
                );

        log.info(
                "Fetched bookings for hotel. hotelId={}, count={}",
                hotelId,
                bookings.size()
        );

        return bookings;
    }

    @Override
    public BookingEntity updateBooking(
            String bookingId,
            BookingEntity booking
    ) {

        log.info(
                "Starting booking update. bookingId={}",
                bookingId
        );

        BookingEntity existingBooking =
                getBooking(bookingId);

        log.debug(
                "Validating updated booking dates. bookingId={}, checkInDate={}, checkOutDate={}",
                bookingId,
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

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

        BookingEntity updatedBooking =
                bookingRepository.save(
                        existingBooking
                );

        log.info(
                "Booking updated successfully. bookingId={}, status={}",
                updatedBooking.getBookingId(),
                updatedBooking.getBookingStatus()
        );

        return updatedBooking;
    }

    @Override
    public void deleteBooking(
            String bookingId
    ) {

        log.info(
                "Starting booking deletion. bookingId={}",
                bookingId
        );

        BookingEntity bookingEntity =
                getBooking(bookingId);

        bookingRepository.delete(
                bookingEntity
        );

        log.info(
                "Booking deleted successfully. bookingId={}",
                bookingId
        );
    }

    @Override
    public BookingPaymentDetails getPaymentDetails(
            String bookingId
    ) {

        log.info(
                "Fetching payment details for bookingId={}",
                bookingId
        );

        BookingEntity booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Cannot fetch payment details because booking was not found. bookingId={}",
                                    bookingId
                            );

                            return new ResourceNotFoundException(
                                    "Booking not found with ID: "
                                            + bookingId
                            );
                        });

        BigDecimal amount =
                convertToBigDecimal(
                        booking.getTotalAmount()
                );

        log.debug(
                "Booking amount converted successfully. bookingId={}, amount={}",
                bookingId,
                amount
        );

        BookingPaymentDetails paymentDetails =
                new BookingPaymentDetails(
                        booking.getBookingId(),
                        amount,
                        booking.getBookingStatus()
                );

        log.info(
                "Payment details prepared successfully. bookingId={}, amount={}, status={}",
                bookingId,
                amount,
                booking.getBookingStatus()
        );

        return paymentDetails;
    }

    private BigDecimal convertToBigDecimal(
            Object totalAmount
    ) {

        if (totalAmount == null) {

            log.warn(
                    "Booking amount conversion failed because amount is null"
            );

            throw new IllegalArgumentException(
                    "Booking amount is required"
            );
        }

        if (totalAmount instanceof BigDecimal amount) {

            log.trace(
                    "Booking amount is already BigDecimal. amount={}",
                    amount
            );

            return amount;
        }

        if (totalAmount instanceof Number number) {

            BigDecimal amount =
                    BigDecimal.valueOf(
                            number.doubleValue()
                    );

            log.trace(
                    "Converted numeric booking amount to BigDecimal. amount={}",
                    amount
            );

            return amount;
        }

        try {

            BigDecimal amount =
                    new BigDecimal(
                            totalAmount.toString()
                    );

            log.trace(
                    "Converted booking amount string to BigDecimal. amount={}",
                    amount
            );

            return amount;

        } catch (NumberFormatException exception) {

            log.warn(
                    "Invalid booking amount received. value={}",
                    totalAmount
            );

            throw new IllegalArgumentException(
                    "Invalid booking amount"
            );
        }
    }

    private void validateBookingDates(
            String checkInDate,
            String checkOutDate
    ) {

        log.trace(
                "Validating booking dates. checkInDate={}, checkOutDate={}",
                checkInDate,
                checkOutDate
        );

        if (checkInDate == null
                || checkOutDate == null) {

            log.warn(
                    "Booking date validation failed because check-in or check-out date is missing. checkInDate={}, checkOutDate={}",
                    checkInDate,
                    checkOutDate
            );

            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required"
            );
        }

        try {

            LocalDate checkIn =
                    LocalDate.parse(
                            checkInDate
                    );

            LocalDate checkOut =
                    LocalDate.parse(
                            checkOutDate
                    );

            if (!checkOut.isAfter(checkIn)) {

                log.warn(
                        "Invalid booking date range. checkInDate={}, checkOutDate={}",
                        checkInDate,
                        checkOutDate
                );

                throw new IllegalArgumentException(
                        "Check-out date must be after check-in date"
                );
            }

            log.trace(
                    "Booking dates validated successfully. checkInDate={}, checkOutDate={}",
                    checkInDate,
                    checkOutDate
            );

        } catch (DateTimeParseException exception) {

            log.warn(
                    "Invalid booking date format. checkInDate={}, checkOutDate={}",
                    checkInDate,
                    checkOutDate
            );

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

        log.info(
                "Checking room availability. roomId={}, checkInDate={}, checkOutDate={}",
                roomId,
                checkInDate,
                checkOutDate
        );

        if (roomId == null || roomId.isBlank()) {

            log.warn(
                    "Room availability check failed because roomId is missing"
            );

            throw new IllegalArgumentException(
                    "Room ID is required"
            );
        }

        if (checkInDate == null
                || checkOutDate == null) {

            log.warn(
                    "Room availability check failed because dates are missing. roomId={}, checkInDate={}, checkOutDate={}",
                    roomId,
                    checkInDate,
                    checkOutDate
            );

            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required"
            );
        }

        if (!checkOutDate.isAfter(checkInDate)) {

            log.warn(
                    "Invalid room availability date range. roomId={}, checkInDate={}, checkOutDate={}",
                    roomId,
                    checkInDate,
                    checkOutDate
            );

            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date"
            );
        }

        List<BookingEntity> bookings =
                bookingRepository.findByRoomId(
                        roomId
                );

        log.debug(
                "Existing bookings loaded for room. roomId={}, bookingCount={}",
                roomId,
                bookings.size()
        );

        for (BookingEntity booking : bookings) {

            if ("CANCELLED".equalsIgnoreCase(
                    booking.getBookingStatus()
            )) {

                log.trace(
                        "Ignoring cancelled booking during availability check. bookingId={}, roomId={}",
                        booking.getBookingId(),
                        roomId
                );

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

            boolean overlap =
                    checkInDate.isBefore(
                            existingCheckOut
                    )
                            &&
                            checkOutDate.isAfter(
                                    existingCheckIn
                            );

            if (overlap) {

                log.info(
                        "Room is not available due to overlapping booking. roomId={}, conflictingBookingId={}, existingCheckIn={}, existingCheckOut={}",
                        roomId,
                        booking.getBookingId(),
                        existingCheckIn,
                        existingCheckOut
                );

                return false;
            }
        }

        log.info(
                "Room is available. roomId={}, checkInDate={}, checkOutDate={}",
                roomId,
                checkInDate,
                checkOutDate
        );

        return true;
    }
}