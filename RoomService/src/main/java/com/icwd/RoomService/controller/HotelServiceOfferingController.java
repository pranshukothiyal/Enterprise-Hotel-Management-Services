package com.icwd.RoomService.controller;

import com.icwd.RoomService.entity.HotelServiceOffering;
import com.icwd.RoomService.entity.ServiceStatus;
import com.icwd.RoomService.service.HotelServiceOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotelservices")
@RequiredArgsConstructor
public class HotelServiceOfferingController {

    private final HotelServiceOfferingService service;

    @PostMapping
    public ResponseEntity<HotelServiceOffering> create(
            @Valid
            @RequestBody HotelServiceOffering offering
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(offering));
    }

    @GetMapping
    public ResponseEntity<List<HotelServiceOffering>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<HotelServiceOffering> getById(
            @PathVariable String serviceId
    ) {
        return ResponseEntity.ok(
                service.getById(serviceId)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelServiceOffering>>
    getByHotel(
            @PathVariable String hotelId
    ) {
        return ResponseEntity.ok(
                service.getByHotelId(hotelId)
        );
    }

    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<HotelServiceOffering>>
    getAvailableByHotel(
            @PathVariable String hotelId
    ) {
        return ResponseEntity.ok(
                service.getAvailableByHotelId(hotelId)
        );
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<HotelServiceOffering> update(
            @PathVariable String serviceId,
            @Valid
            @RequestBody HotelServiceOffering offering
    ) {
        return ResponseEntity.ok(
                service.update(serviceId, offering)
        );
    }

    @PatchMapping("/{serviceId}/status")
    public ResponseEntity<HotelServiceOffering> updateStatus(
            @PathVariable String serviceId,
            @RequestParam ServiceStatus status
    ) {
        return ResponseEntity.ok(
                service.updateStatus(serviceId, status)
        );
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(
            @PathVariable String serviceId
    ) {
        service.delete(serviceId);
        return ResponseEntity.noContent().build();
    }
}