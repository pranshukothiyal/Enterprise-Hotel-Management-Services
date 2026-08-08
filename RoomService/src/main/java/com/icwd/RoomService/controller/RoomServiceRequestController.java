package com.icwd.RoomService.controller;

import com.icwd.RoomService.dto.CreateRoomServiceRequest;
import com.icwd.RoomService.entity.RequestStatus;
import com.icwd.RoomService.entity.RoomServiceRequest;
import com.icwd.RoomService.service.RoomServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roomservicerequests")
@RequiredArgsConstructor
public class RoomServiceRequestController {

    private final RoomServiceRequestService service;

    @PostMapping
    public ResponseEntity<RoomServiceRequest> create(
            @Valid
            @RequestBody CreateRoomServiceRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createRequest(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomServiceRequest>> getAll() {
        return ResponseEntity.ok(
                service.getAllRequests()
        );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RoomServiceRequest> getById(
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(
                service.getRequestById(requestId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomServiceRequest>> getByUser(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(
                service.getByUserId(userId)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<RoomServiceRequest>>
    getByBooking(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(
                service.getByBookingId(bookingId)
        );
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomServiceRequest>> getByRoom(
            @PathVariable String roomId
    ) {
        return ResponseEntity.ok(
                service.getByRoomId(roomId)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<RoomServiceRequest>>
    getByEmployee(
            @PathVariable String employeeId
    ) {
        return ResponseEntity.ok(
                service.getByEmployeeId(employeeId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomServiceRequest>> getByStatus(
            @PathVariable RequestStatus status
    ) {
        return ResponseEntity.ok(
                service.getByStatus(status)
        );
    }

    @PatchMapping("/{requestId}/assign/{employeeId}")
    public ResponseEntity<RoomServiceRequest> assignEmployee(
            @PathVariable String requestId,
            @PathVariable String employeeId
    ) {
        return ResponseEntity.ok(
                service.assignEmployee(
                        requestId,
                        employeeId
                )
        );
    }

    @PatchMapping("/{requestId}/status")
    public ResponseEntity<RoomServiceRequest> updateStatus(
            @PathVariable String requestId,
            @RequestParam RequestStatus status
    ) {
        return ResponseEntity.ok(
                service.updateStatus(requestId, status)
        );
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RoomServiceRequest> cancel(
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(
                service.cancelRequest(requestId)
        );
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> delete(
            @PathVariable String requestId
    ) {
        service.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}