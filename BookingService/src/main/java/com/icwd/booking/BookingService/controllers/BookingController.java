package com.icwd.booking.BookingService.controllers;

import com.icwd.booking.BookingService.entitites.BookingEntity;
import com.icwd.booking.BookingService.payload.ApiResponse;
import com.icwd.booking.BookingService.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final Service bookingService;

    public BookingController(Service bookingService) {
        this.bookingService = bookingService;
    }

    // Create booking
    @PostMapping
    public ResponseEntity<BookingEntity> createBooking(
            @RequestBody BookingEntity bookingEntity
    ) {

        BookingEntity booking =
                bookingService.createBooking(bookingEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(booking);
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<BookingEntity>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings()
        );
    }

    // Get booking by ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingEntity> getBooking(
            @PathVariable String bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.getBooking(bookingId)
        );
    }

    // Get bookings by user ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<BookingEntity>> getBookingsByUser(
            @PathVariable String userId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingsByUser(userId)
        );
    }

    // Get bookings by hotel ID
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<BookingEntity>> getBookingsByHotel(
            @PathVariable String hotelId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingsByHotel(hotelId)
        );
    }

    // Update booking
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingEntity> updateBooking(
            @PathVariable String bookingId,
            @RequestBody BookingEntity bookingEntity
    ) {

        return ResponseEntity.ok(
                bookingService.updateBooking(
                        bookingId,
                        bookingEntity
                )
        );
    }

    // Delete booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> deleteBooking(
            @PathVariable String bookingId
    ) {

        bookingService.deleteBooking(bookingId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Booking deleted successfully",
                        true
                )
        );
    }
}