package com.icwd.RoomService.service;

import com.icwd.RoomService.dto.CreateRoomServiceRequest;
import com.icwd.RoomService.entity.*;
import com.icwd.RoomService.exception.ResourceNotFoundException;
import com.icwd.RoomService.repository.HotelServiceOfferingRepository;
import com.icwd.RoomService.repository.RoomServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceRequestService {

    private final RoomServiceRequestRepository requestRepository;
    private final HotelServiceOfferingRepository offeringRepository;

    public RoomServiceRequest createRequest(
            CreateRoomServiceRequest request
    ) {

        log.info(
                "Starting room service request creation. serviceId={}, bookingId={}, roomId={}",
                request.serviceId(),
                request.bookingId(),
                request.roomId()
        );

        HotelServiceOffering offering =
                offeringRepository
                        .findById(request.serviceId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Hotel service offering not found while creating room service request. serviceId={}",
                                    request.serviceId()
                            );

                            return new ResourceNotFoundException(
                                    "Hotel service not found: "
                                            + request.serviceId()
                            );
                        });

        log.debug(
                "Hotel service offering found. serviceId={}, status={}",
                request.serviceId(),
                offering.getServiceStatus()
        );

        if (offering.getServiceStatus()
                != ServiceStatus.AVAILABLE) {

            log.warn(
                    "Room service request rejected because selected service is unavailable. serviceId={}, status={}",
                    request.serviceId(),
                    offering.getServiceStatus()
            );

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

        log.debug(
                "Calculated room service request total. serviceId={}, quantity={}, totalAmount={}",
                request.serviceId(),
                request.quantity(),
                totalAmount
        );

        String requestId =
                "REQ-" + UUID.randomUUID();

        log.debug(
                "Generated room service request ID. requestId={}",
                requestId
        );

        RoomServiceRequest entity =
                RoomServiceRequest.builder()
                        .requestId(
                                requestId
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

        RoomServiceRequest savedRequest =
                requestRepository.save(
                        entity
                );

        log.info(
                "Room service request created successfully. requestId={}, bookingId={}, status={}",
                requestId,
                request.bookingId(),
                RequestStatus.PENDING
        );

        return savedRequest;
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getAllRequests() {

        log.debug(
                "Fetching all room service requests from repository"
        );

        List<RoomServiceRequest> requests =
                requestRepository.findAll();

        log.info(
                "Room service requests fetched successfully. count={}",
                requests.size()
        );

        return requests;
    }

    @Transactional(readOnly = true)
    public RoomServiceRequest getRequestById(
            String requestId
    ) {

        log.debug(
                "Fetching room service request by ID. requestId={}",
                requestId
        );

        return requestRepository
                .findById(requestId)
                .orElseThrow(() -> {

                    log.warn(
                            "Room service request not found. requestId={}",
                            requestId
                    );

                    return new ResourceNotFoundException(
                            "Room-service request not found: "
                                    + requestId
                    );
                });
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByUserId(
            String userId
    ) {

        log.debug(
                "Fetching room service requests by user. userId={}",
                userId
        );

        List<RoomServiceRequest> requests =
                requestRepository.findByUserId(
                        userId
                );

        log.info(
                "Room service requests fetched successfully for user. userId={}, count={}",
                userId,
                requests.size()
        );

        return requests;
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByBookingId(
            String bookingId
    ) {

        log.debug(
                "Fetching room service requests by booking. bookingId={}",
                bookingId
        );

        List<RoomServiceRequest> requests =
                requestRepository
                        .findByBookingId(
                                bookingId
                        );

        log.info(
                "Room service requests fetched successfully for booking. bookingId={}, count={}",
                bookingId,
                requests.size()
        );

        return requests;
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByRoomId(
            String roomId
    ) {

        log.debug(
                "Fetching room service requests by room. roomId={}",
                roomId
        );

        List<RoomServiceRequest> requests =
                requestRepository.findByRoomId(
                        roomId
                );

        log.info(
                "Room service requests fetched successfully for room. roomId={}, count={}",
                roomId,
                requests.size()
        );

        return requests;
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByEmployeeId(
            String employeeId
    ) {

        log.debug(
                "Fetching room service requests by assigned employee. employeeId={}",
                employeeId
        );

        List<RoomServiceRequest> requests =
                requestRepository
                        .findByAssignedEmployeeId(
                                employeeId
                        );

        log.info(
                "Room service requests fetched successfully for employee. employeeId={}, count={}",
                employeeId,
                requests.size()
        );

        return requests;
    }

    @Transactional(readOnly = true)
    public List<RoomServiceRequest> getByStatus(
            RequestStatus status
    ) {

        log.debug(
                "Fetching room service requests by status. status={}",
                status
        );

        List<RoomServiceRequest> requests =
                requestRepository
                        .findByRequestStatus(
                                status
                        );

        log.info(
                "Room service requests fetched successfully by status. status={}, count={}",
                status,
                requests.size()
        );

        return requests;
    }

    public RoomServiceRequest assignEmployee(
            String requestId,
            String employeeId
    ) {

        log.info(
                "Starting employee assignment to room service request. requestId={}, employeeId={}",
                requestId,
                employeeId
        );

        RoomServiceRequest request =
                getRequestById(
                        requestId
                );

        if (request.getRequestStatus()
                == RequestStatus.COMPLETED
                || request.getRequestStatus()
                == RequestStatus.CANCELLED) {

            log.warn(
                    "Employee assignment rejected due to request status. requestId={}, status={}",
                    requestId,
                    request.getRequestStatus()
            );

            throw new IllegalStateException(
                    "Cannot assign employee to a completed or cancelled request"
            );
        }

        request.setAssignedEmployeeId(
                employeeId
        );

        if (request.getRequestStatus()
                == RequestStatus.PENDING) {

            request.setRequestStatus(
                    RequestStatus.ACCEPTED
            );

            log.debug(
                    "Room service request status automatically changed from PENDING to ACCEPTED. requestId={}",
                    requestId
            );
        }

        RoomServiceRequest savedRequest =
                requestRepository.save(
                        request
                );

        log.info(
                "Employee assigned successfully to room service request. requestId={}, employeeId={}, status={}",
                requestId,
                employeeId,
                savedRequest.getRequestStatus()
        );

        return savedRequest;
    }

    public RoomServiceRequest updateStatus(
            String requestId,
            RequestStatus status
    ) {

        log.info(
                "Starting room service request status update. requestId={}, newStatus={}",
                requestId,
                status
        );

        RoomServiceRequest request =
                getRequestById(
                        requestId
                );

        RequestStatus previousStatus =
                request.getRequestStatus();

        request.setRequestStatus(
                status
        );

        if (status == RequestStatus.COMPLETED) {

            request.setCompletedAt(
                    LocalDateTime.now()
            );

            log.debug(
                    "Completion timestamp set for room service request. requestId={}",
                    requestId
            );

        } else {

            request.setCompletedAt(
                    null
            );
        }

        RoomServiceRequest savedRequest =
                requestRepository.save(
                        request
                );

        log.info(
                "Room service request status updated successfully. requestId={}, previousStatus={}, newStatus={}",
                requestId,
                previousStatus,
                status
        );

        return savedRequest;
    }

    public RoomServiceRequest cancelRequest(
            String requestId
    ) {

        log.info(
                "Starting room service request cancellation. requestId={}",
                requestId
        );

        RoomServiceRequest request =
                getRequestById(
                        requestId
                );

        if (request.getRequestStatus()
                == RequestStatus.COMPLETED) {

            log.warn(
                    "Room service request cancellation rejected because request is already completed. requestId={}",
                    requestId
            );

            throw new IllegalStateException(
                    "Completed request cannot be cancelled"
            );
        }

        request.setRequestStatus(
                RequestStatus.CANCELLED
        );

        RoomServiceRequest savedRequest =
                requestRepository.save(
                        request
                );

        log.info(
                "Room service request cancelled successfully. requestId={}",
                requestId
        );

        return savedRequest;
    }

    public void deleteRequest(
            String requestId
    ) {

        log.info(
                "Starting room service request deletion. requestId={}",
                requestId
        );

        RoomServiceRequest request =
                getRequestById(
                        requestId
                );

        requestRepository.delete(
                request
        );

        log.info(
                "Room service request deleted successfully. requestId={}",
                requestId
        );
    }
}