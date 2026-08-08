package com.icwd.RoomService.service;

import com.icwd.RoomService.dto.CreateRoomServiceRequest;
import com.icwd.RoomService.entity.*;
import com.icwd.RoomService.exception.ResourceNotFoundException;
import com.icwd.RoomService.repository.HotelServiceOfferingRepository;
import com.icwd.RoomService.repository.RoomServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceRequestService {

    private final RoomServiceRequestRepository requestRepository;
    private final HotelServiceOfferingRepository offeringRepository;

    public RoomServiceRequest createRequest(
            CreateRoomServiceRequest request
    ) {
        HotelServiceOffering offering =
                offeringRepository
                        .findById(request.serviceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Hotel service not found: "
                                                + request.serviceId()
                                )
                        );

        if (offering.getServiceStatus()
                != ServiceStatus.AVAILABLE) {

            throw new IllegalStateException(
                    "Selected hotel service is unavailable"
            );
        }

        BigDecimal totalAmount =
                offering.getPrice().multiply(
                        BigDecimal.valueOf(
                                request.quantity()
                        )
                );

        RoomServiceRequest entity =
                RoomServiceRequest.builder()
                        .requestId(
                                "REQ-" + UUID.randomUUID()
                        )
                        .userId(request.userId())
                        .bookingId(request.bookingId())
                        .roomId(request.roomId())
                        .quantity(request.quantity())
                        .specialInstructions(
                                request.specialInstructions()
                        )
                        .totalAmount(totalAmount)
                        .requestStatus(
                                RequestStatus.PENDING
                        )
                        .serviceOffering(offering)
                        .build();

        return requestRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RoomServiceRequest getRequestById(
            String requestId
    ) {
        return requestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room-service request not found: "
                                        + requestId
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByUserId(
            String userId
    ) {
        return requestRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByBookingId(
            String bookingId
    ) {
        return requestRepository
                .findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByRoomId(
            String roomId
    ) {
        return requestRepository.findByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByEmployeeId(
            String employeeId
    ) {
        return requestRepository
                .findByAssignedEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByStatus(
            RequestStatus status
    ) {
        return requestRepository
                .findByRequestStatus(status);
    }

    public RoomServiceRequest assignEmployee(
            String requestId,
            String employeeId
    ) {
        RoomServiceRequest request =
                getRequestById(requestId);

        if (request.getRequestStatus()
                == RequestStatus.COMPLETED
                || request.getRequestStatus()
                == RequestStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cannot assign employee to a completed or cancelled request"
            );
        }

        request.setAssignedEmployeeId(employeeId);

        if (request.getRequestStatus()
                == RequestStatus.PENDING) {

            request.setRequestStatus(
                    RequestStatus.ACCEPTED
            );
        }

        return requestRepository.save(request);
    }

    public RoomServiceRequest updateStatus(
            String requestId,
            RequestStatus status
    ) {
        RoomServiceRequest request =
                getRequestById(requestId);

        request.setRequestStatus(status);

        if (status == RequestStatus.COMPLETED) {
            request.setCompletedAt(
                    LocalDateTime.now()
            );
        } else {
            request.setCompletedAt(null);
        }

        return requestRepository.save(request);
    }

    public RoomServiceRequest cancelRequest(
            String requestId
    ) {
        RoomServiceRequest request =
                getRequestById(requestId);

        if (request.getRequestStatus()
                == RequestStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed request cannot be cancelled"
            );
        }

        request.setRequestStatus(
                RequestStatus.CANCELLED
        );

        return requestRepository.save(request);
    }

    public void deleteRequest(String requestId) {
        RoomServiceRequest request =
                getRequestById(requestId);

        requestRepository.delete(request);
    }
}