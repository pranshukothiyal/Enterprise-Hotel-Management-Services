package com.icwd.invoice.feign;

import com.icwd.invoice.dto.BookingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingServiceClient {

    @GetMapping("/api/bookings/{bookingId}")
    BookingDto getBookingById(@PathVariable String bookingId);
}
