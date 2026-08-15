package com.icwd.RoomService.controller;

import com.icwd.RoomService.entity.HotelServiceOffering;
import com.icwd.RoomService.entity.ServiceStatus;
import com.icwd.RoomService.service.HotelServiceOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info(
                "Received request to create hotel service offering"
        );

        HotelServiceOffering createdOffering =
                service.create(offering);

        log.info(
                "Hotel service offering created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOffering);
    }

    @GetMapping
    public ResponseEntity<List<HotelServiceOffering>> getAll() {

        log.info(
                "Received request to fetch all hotel service offerings"
        );

        List<HotelServiceOffering> offerings =
                service.getAll();

        log.debug(
                "Fetched all hotel service offerings successfully. count={}",
                offerings.size()
        );

        return ResponseEntity.ok(
                offerings
        );
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<HotelServiceOffering> getById(
            @PathVariable String serviceId
    ) {

        log.info(
                "Received request to fetch hotel service offering. serviceId={}",
                serviceId
        );

        HotelServiceOffering offering =
                service.getById(serviceId);

        log.debug(
                "Hotel service offering fetched successfully. serviceId={}",
                serviceId
        );

        return ResponseEntity.ok(
                offering
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelServiceOffering>>
    getByHotel(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch hotel service offerings by hotel. hotelId={}",
                hotelId
        );

        List<HotelServiceOffering> offerings =
                service.getByHotelId(hotelId);

        log.debug(
                "Hotel service offerings fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                offerings.size()
        );

        return ResponseEntity.ok(
                offerings
        );
    }

    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<HotelServiceOffering>>
    getAvailableByHotel(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch available hotel service offerings. hotelId={}",
                hotelId
        );

        List<HotelServiceOffering> offerings =
                service.getAvailableByHotelId(hotelId);

        log.debug(
                "Available hotel service offerings fetched successfully. hotelId={}, count={}",
                hotelId,
                offerings.size()
        );

        return ResponseEntity.ok(
                offerings
        );
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<HotelServiceOffering> update(
            @PathVariable String serviceId,
            @Valid
            @RequestBody HotelServiceOffering offering
    ) {

        log.info(
                "Received request to update hotel service offering. serviceId={}",
                serviceId
        );

        HotelServiceOffering updatedOffering =
                service.update(
                        serviceId,
                        offering
                );

        log.info(
                "Hotel service offering updated successfully. serviceId={}",
                serviceId
        );

        return ResponseEntity.ok(
                updatedOffering
        );
    }

    @PatchMapping("/{serviceId}/status")
    public ResponseEntity<HotelServiceOffering> updateStatus(
            @PathVariable String serviceId,
            @RequestParam ServiceStatus status
    ) {

        log.info(
                "Received request to update hotel service offering status. serviceId={}, status={}",
                serviceId,
                status
        );

        HotelServiceOffering updatedOffering =
                service.updateStatus(
                        serviceId,
                        status
                );

        log.info(
                "Hotel service offering status updated successfully. serviceId={}, status={}",
                serviceId,
                status
        );

        return ResponseEntity.ok(
                updatedOffering
        );
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(
            @PathVariable String serviceId
    ) {

        log.info(
                "Received request to delete hotel service offering. serviceId={}",
                serviceId
        );

        service.delete(serviceId);

        log.info(
                "Hotel service offering deleted successfully. serviceId={}",
                serviceId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}