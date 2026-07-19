package com.icwd.hotel.repositories;

import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {

    List<Room> findByHotel_Id(String hotelId);

    List<Room> findByHotel_IdAndRoomStatus(
            String hotelId,
            RoomStatus roomStatus
    );

    List<Room> findByHotel_IdAndRoomType(
            String hotelId,
            RoomType roomType
    );

    boolean existsByHotel_IdAndRoomNumber(
            String hotelId,
            String roomNumber
    );
}