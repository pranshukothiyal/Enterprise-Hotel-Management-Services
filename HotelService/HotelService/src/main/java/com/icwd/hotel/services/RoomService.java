package com.icwd.hotel.services;

import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;

import java.util.List;

public interface RoomService {

    Room createRoom(String hotelId, Room room);

    List<Room> getAllRooms();

    Room getRoomById(String roomId);

    List<Room> getRoomsByHotel(String hotelId);

    List<Room> getRoomsByHotelAndStatus(
            String hotelId,
            RoomStatus roomStatus
    );

    List<Room> getRoomsByHotelAndType(
            String hotelId,
            RoomType roomType
    );

    Room updateRoom(String roomId, Room room);

    Room updateRoomStatus(
            String roomId,
            RoomStatus roomStatus
    );

    void deleteRoom(String roomId);
}