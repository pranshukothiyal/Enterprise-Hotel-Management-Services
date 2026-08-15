package com.icwd.AIAssistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingClient {

    @GetMapping("/bookings/users/{userId}")
    List<Map<String, Object>> getBookingsByUserId(
            @PathVariable("userId") String userId
    );

    @GetMapping("/bookings/availability")
    Map<String, Object> checkAvailability(
            @RequestParam("roomId") String roomId,

            @RequestParam("checkInDate")
            LocalDate checkInDate,

            @RequestParam("checkOutDate")
            LocalDate checkOutDate
    );
}