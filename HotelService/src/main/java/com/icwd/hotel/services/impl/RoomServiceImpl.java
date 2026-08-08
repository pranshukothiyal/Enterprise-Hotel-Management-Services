package com.icwd.hotel.services.impl;

import com.icwd.hotel.entities.Hotel;
import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;
import com.icwd.hotel.exception.ResourceNotFoundException;
import com.icwd.hotel.repository.HotelRepository;
import com.icwd.hotel.repositories.RoomRepository;
import com.icwd.hotel.services.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            HotelRepository hotelRepository
    ) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Room createRoom(String hotelId, Room room) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + hotelId
                        )
                );

        if (roomRepository.existsByHotel_IdAndRoomNumber(
                hotelId,
                room.getRoomNumber()
        )) {
            throw new IllegalArgumentException(
                    "Room number already exists in this hotel"
            );
        }

        room.setRoomId(UUID.randomUUID().toString());
        room.setHotel(hotel);

        if (room.getRoomStatus() == null) {
            room.setRoomStatus(RoomStatus.AVAILABLE);
        }

        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(String roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with ID: " + roomId
                        )
                );
    }

    @Override
    public List<Room> getRoomsByHotel(String hotelId) {

        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException(
                    "Hotel not found with ID: " + hotelId
            );
        }

        return roomRepository.findByHotel_Id(hotelId);
    }

    @Override
    public List<Room> getRoomsByHotelAndStatus(
            String hotelId,
            RoomStatus roomStatus
    ) {

        return roomRepository.findByHotel_IdAndRoomStatus(
                hotelId,
                roomStatus
        );
    }

    @Override
    public List<Room> getRoomsByHotelAndType(
            String hotelId,
            RoomType roomType
    ) {

        return roomRepository.findByHotel_IdAndRoomType(
                hotelId,
                roomType
        );
    }

    @Override
    @Transactional
    public Room updateRoom(String roomId, Room newRoom) {

        Room existingRoom = getRoomById(roomId);

        existingRoom.setRoomNumber(newRoom.getRoomNumber());
        existingRoom.setRoomType(newRoom.getRoomType());
        existingRoom.setPricePerNight(
                newRoom.getPricePerNight()
        );
        existingRoom.setCapacity(newRoom.getCapacity());
        existingRoom.setFloorNumber(
                newRoom.getFloorNumber()
        );

        if (newRoom.getRoomStatus() != null) {
            existingRoom.setRoomStatus(
                    newRoom.getRoomStatus()
            );
        }

        return roomRepository.save(existingRoom);
    }

    @Override
    @Transactional
    public Room updateRoomStatus(
            String roomId,
            RoomStatus roomStatus
    ) {

        Room room = getRoomById(roomId);
        room.setRoomStatus(roomStatus);

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(String roomId) {

        Room room = getRoomById(roomId);
        roomRepository.delete(room);
    }
}