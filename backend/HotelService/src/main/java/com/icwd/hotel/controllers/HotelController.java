package com.icwd.hotel.controllers;

import com.icwd.hotel.entities.Hotel;
import com.icwd.hotel.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // create
    @PostMapping
    public ResponseEntity<?> createHotel(
            @RequestBody Hotel hotel
    ) {

        log.info(
                "Received request to create hotel"
        );

        Hotel createdHotel =
                hotelService.create(hotel);

        log.info(
                "Hotel created successfully. hotelId={}",
                createdHotel.getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdHotel);
    }

    // get single
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getSingleHotel(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch hotel. hotelId={}",
                hotelId
        );

        Hotel hotel =
                hotelService.get(hotelId);

        log.debug(
                "Hotel fetched successfully. hotelId={}",
                hotelId
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(hotel);
    }

    // get all
    @GetMapping
    public ResponseEntity<?> getAll() {

        log.info(
                "Received request to fetch all hotels"
        );

        var hotels =
                hotelService.getAll();

        log.debug(
                "Fetched all hotels successfully. count={}",
                hotels.size()
        );

        return ResponseEntity.ok(
                hotels
        );
    }
}