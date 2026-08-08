package com.icwd.AIAssistant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelClient {

    @GetMapping("/hotels")
    List<Map<String, Object>> getAllHotels();

    @GetMapping("/rooms")
    List<Map<String, Object>> getAllRooms();

    @GetMapping("/hotels/{hotelId}")
    Map<String, Object> getHotelById(
            @PathVariable String hotelId
    );

    @GetMapping("/hotels/search")
    List<Map<String, Object>> getHotelsByLocation(
            @RequestParam String location
    );

    @GetMapping("/rooms/hotel/{hotelId}")
    List<Map<String, Object>> getRoomsByHotelId(
            @PathVariable String hotelId
    );
}