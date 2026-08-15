package com.icwd.RoomService.controller;

import com.icwd.RoomService.dto.CreateRoomServiceRequest;
import com.icwd.RoomService.entity.RequestStatus;
import com.icwd.RoomService.entity.RoomServiceRequest;
import com.icwd.RoomService.service.RoomServiceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info(
                "Received request to create room service request"
        );

        RoomServiceRequest createdRequest =
                service.createRequest(request);

        log.info(
                "Room service request created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    @GetMapping
    public ResponseEntity<List<RoomServiceRequest>> getAll() {

        log.info(
                "Received request to fetch all room service requests"
        );

        List<RoomServiceRequest> requests =
                service.getAllRequests();

        log.debug(
                "Fetched all room service requests successfully. count={}",
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RoomServiceRequest> getById(
            @PathVariable String requestId
    ) {

        log.info(
                "Received request to fetch room service request. requestId={}",
                requestId
        );

        RoomServiceRequest request =
                service.getRequestById(
                        requestId
                );

        log.debug(
                "Room service request fetched successfully. requestId={}",
                requestId
        );

        return ResponseEntity.ok(
                request
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomServiceRequest>> getByUser(
            @PathVariable String userId
    ) {

        log.info(
                "Received request to fetch room service requests by user. userId={}",
                userId
        );

        List<RoomServiceRequest> requests =
                service.getByUserId(
                        userId
                );

        log.debug(
                "Room service requests fetched successfully for user. userId={}, count={}",
                userId,
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<RoomServiceRequest>>
    getByBooking(
            @PathVariable String bookingId
    ) {

        log.info(
                "Received request to fetch room service requests by booking. bookingId={}",
                bookingId
        );

        List<RoomServiceRequest> requests =
                service.getByBookingId(
                        bookingId
                );

        log.debug(
                "Room service requests fetched successfully for booking. bookingId={}, count={}",
                bookingId,
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomServiceRequest>> getByRoom(
            @PathVariable String roomId
    ) {

        log.info(
                "Received request to fetch room service requests by room. roomId={}",
                roomId
        );

        List<RoomServiceRequest> requests =
                service.getByRoomId(
                        roomId
                );

        log.debug(
                "Room service requests fetched successfully for room. roomId={}, count={}",
                roomId,
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<RoomServiceRequest>>
    getByEmployee(
            @PathVariable String employeeId
    ) {

        log.info(
                "Received request to fetch room service requests by employee. employeeId={}",
                employeeId
        );

        List<RoomServiceRequest> requests =
                service.getByEmployeeId(
                        employeeId
                );

        log.debug(
                "Room service requests fetched successfully for employee. employeeId={}, count={}",
                employeeId,
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomServiceRequest>> getByStatus(
            @PathVariable RequestStatus status
    ) {

        log.info(
                "Received request to fetch room service requests by status. status={}",
                status
        );

        List<RoomServiceRequest> requests =
                service.getByStatus(
                        status
                );

        log.debug(
                "Room service requests fetched successfully by status. status={}, count={}",
                status,
                requests.size()
        );

        return ResponseEntity.ok(
                requests
        );
    }

    @PatchMapping("/{requestId}/assign/{employeeId}")
    public ResponseEntity<RoomServiceRequest> assignEmployee(
            @PathVariable String requestId,
            @PathVariable String employeeId
    ) {

        log.info(
                "Received request to assign employee to room service request. requestId={}, employeeId={}",
                requestId,
                employeeId
        );

        RoomServiceRequest updatedRequest =
                service.assignEmployee(
                        requestId,
                        employeeId
                );

        log.info(
                "Employee assigned successfully to room service request. requestId={}, employeeId={}",
                requestId,
                employeeId
        );

        return ResponseEntity.ok(
                updatedRequest
        );
    }

    @PatchMapping("/{requestId}/status")
    public ResponseEntity<RoomServiceRequest> updateStatus(
            @PathVariable String requestId,
            @RequestParam RequestStatus status
    ) {

        log.info(
                "Received request to update room service request status. requestId={}, status={}",
                requestId,
                status
        );

        RoomServiceRequest updatedRequest =
                service.updateStatus(
                        requestId,
                        status
                );

        log.info(
                "Room service request status updated successfully. requestId={}, status={}",
                requestId,
                status
        );

        return ResponseEntity.ok(
                updatedRequest
        );
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RoomServiceRequest> cancel(
            @PathVariable String requestId
    ) {

        log.info(
                "Received request to cancel room service request. requestId={}",
                requestId
        );

        RoomServiceRequest cancelledRequest =
                service.cancelRequest(
                        requestId
                );

        log.info(
                "Room service request cancelled successfully. requestId={}",
                requestId
        );

        return ResponseEntity.ok(
                cancelledRequest
        );
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> delete(
            @PathVariable String requestId
    ) {

        log.info(
                "Received request to delete room service request. requestId={}",
                requestId
        );

        service.deleteRequest(
                requestId
        );

        log.info(
                "Room service request deleted successfully. requestId={}",
                requestId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}