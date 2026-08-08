package com.icwd.payment.client;

import com.icwd.payment.dto.BookingPaymentDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingService{

    @GetMapping(
            "/bookings/{bookingId}/payment-details"
    )
    BookingPaymentDetails getPaymentDetails(
            @PathVariable("bookingId")
            String bookingId
    );
}