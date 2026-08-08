package com.icwd.AIAssistant.controller;

import com.icwd.AIAssistant.client.HotelClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/debug")
public class AIDebugController {

    private final HotelClient hotelClient;

    public AIDebugController(HotelClient hotelClient) {
        this.hotelClient = hotelClient;
    }

    @GetMapping("/hotels")
    public List<Map<String, Object>> getHotels() {
        return hotelClient.getAllHotels();
    }

    @GetMapping("/rooms")
    public List<Map<String, Object>> getRooms() {
        return hotelClient.getAllRooms();
    }
}