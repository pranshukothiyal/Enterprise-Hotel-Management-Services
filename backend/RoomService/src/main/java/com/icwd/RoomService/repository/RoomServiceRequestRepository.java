package com.icwd.RoomService.repository;

import com.icwd.RoomService.entity.RequestStatus;
import com.icwd.RoomService.entity.RoomServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomServiceRequestRepository
        extends JpaRepository<RoomServiceRequest, String> {

    List<RoomServiceRequest> findByUserId(
            String userId
    );

    List<RoomServiceRequest> findByBookingId(
            String bookingId
    );

    List<RoomServiceRequest> findByRoomId(
            String roomId
    );

    List<RoomServiceRequest> findByAssignedEmployeeId(
            String employeeId
    );

    List<RoomServiceRequest> findByRequestStatus(
            RequestStatus requestStatus
    );
}