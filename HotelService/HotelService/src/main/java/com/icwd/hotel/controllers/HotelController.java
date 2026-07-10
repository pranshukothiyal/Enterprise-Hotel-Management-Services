package com.icwd.hotel.controllers;

import com.icwd.hotel.entities.Hotel;
import com.icwd.hotel.services.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    //create
    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody Hotel hotel){
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(hotel));
    }

    //    get single
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getSingleHotel(@PathVariable String hotelId){
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.get(hotelId));
    }


    //    get all
    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(hotelService.getAll());
    }
}