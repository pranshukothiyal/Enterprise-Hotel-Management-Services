package com.icwd.AIAssistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "RATING-SERVICE")
public interface RatingClient {

    @GetMapping("/ratings/hotels/{hotelId}")
    List<Map<String, Object>> getRatingsByHotelId(
            @PathVariable String hotelId
    );
}