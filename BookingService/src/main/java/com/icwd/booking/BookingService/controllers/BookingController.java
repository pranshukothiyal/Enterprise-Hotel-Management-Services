package com.icwd.booking.BookingService.controllers;

import com.icwd.booking.BookingService.dto.BookingPaymentDetails;
import com.icwd.booking.BookingService.entitites.BookingEntity;
import com.icwd.booking.BookingService.payload.ApiResponse;
import com.icwd.booking.BookingService.services.Service;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{bookingId}/payment-details")
    public ResponseEntity<BookingPaymentDetails> getPaymentDetails(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(
                bookingService.getPaymentDetails(
                        bookingId
                )
        );
    }

    // --- ADDED: Check room availability for AI Assistant & Feign Client ---
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam("roomId") String roomId,

            @RequestParam("checkInDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkInDate,

            @RequestParam("checkOutDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOutDate
    ) {
        // If your Service interface has a checkAvailability method:
        // return ResponseEntity.ok(bookingService.checkAvailability(roomId, checkInDate, checkOutDate));

        // Alternative logic if checking against existing bookings:
        boolean isAvailable = bookingService.isRoomAvailable(roomId, checkInDate, checkOutDate);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", roomId);
        response.put("checkInDate", checkInDate.toString());
        response.put("checkOutDate", checkOutDate.toString());
        response.put("isAvailable", isAvailable);
        response.put("status", isAvailable ? "AVAILABLE" : "BOOKED");

        return ResponseEntity.ok(response);
    }
}