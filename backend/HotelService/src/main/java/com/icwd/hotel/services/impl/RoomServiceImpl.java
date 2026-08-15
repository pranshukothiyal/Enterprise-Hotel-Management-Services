package com.icwd.hotel.services.impl;

import com.icwd.hotel.entities.Hotel;
import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;
import com.icwd.hotel.exception.ResourceNotFoundException;
import com.icwd.hotel.repository.HotelRepository;
import com.icwd.hotel.repositories.RoomRepository;
import com.icwd.hotel.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
    public Room createRoom(
            String hotelId,
            Room room
    ) {

        log.info(
                "Starting room creation. hotelId={}, roomNumber={}",
                hotelId,
                room.getRoomNumber()
        );

        log.debug(
                "Fetching hotel before creating room. hotelId={}",
                hotelId
        );

        Hotel hotel =
                hotelRepository
                        .findById(hotelId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Room creation failed because hotel was not found. hotelId={}",
                                    hotelId
                            );

                            return new ResourceNotFoundException(
                                    "Hotel not found with ID: "
                                            + hotelId
                            );
                        });

        log.debug(
                "Checking duplicate room number. hotelId={}, roomNumber={}",
                hotelId,
                room.getRoomNumber()
        );

        if (roomRepository.existsByHotel_IdAndRoomNumber(
                hotelId,
                room.getRoomNumber()
        )) {

            log.warn(
                    "Room creation rejected because room number already exists. hotelId={}, roomNumber={}",
                    hotelId,
                    room.getRoomNumber()
            );

            throw new IllegalArgumentException(
                    "Room number already exists in this hotel"
            );
        }

        String roomId =
                UUID.randomUUID().toString();

        room.setRoomId(
                roomId
        );

        log.debug(
                "Generated room ID. roomId={}, hotelId={}",
                roomId,
                hotelId
        );

        room.setHotel(
                hotel
        );

        if (room.getRoomStatus() == null) {

            room.setRoomStatus(
                    RoomStatus.AVAILABLE
            );

            log.debug(
                    "Room status was not provided. Default status set to AVAILABLE. roomId={}",
                    roomId
            );
        }

        Room savedRoom =
                roomRepository.save(
                        room
                );

        log.info(
                "Room created successfully. roomId={}, hotelId={}, roomNumber={}, status={}",
                savedRoom.getRoomId(),
                hotelId,
                savedRoom.getRoomNumber(),
                savedRoom.getRoomStatus()
        );

        return savedRoom;
    }

    @Override
    public List<Room> getAllRooms() {

        log.debug(
                "Fetching all rooms from repository"
        );

        List<Room> rooms =
                roomRepository.findAll();

        log.info(
                "Rooms fetched successfully. count={}",
                rooms.size()
        );

        return rooms;
    }

    @Override
    public Room getRoomById(
            String roomId
    ) {

        log.debug(
                "Fetching room by ID. roomId={}",
                roomId
        );

        return roomRepository
                .findById(roomId)
                .orElseThrow(() -> {

                    log.warn(
                            "Room not found. roomId={}",
                            roomId
                    );

                    return new ResourceNotFoundException(
                            "Room not found with ID: "
                                    + roomId
                    );
                });
    }

    @Override
    public List<Room> getRoomsByHotel(
            String hotelId
    ) {

        log.debug(
                "Fetching rooms by hotel. hotelId={}",
                hotelId
        );

        if (!hotelRepository.existsById(
                hotelId
        )) {

            log.warn(
                    "Cannot fetch rooms because hotel was not found. hotelId={}",
                    hotelId
            );

            throw new ResourceNotFoundException(
                    "Hotel not found with ID: "
                            + hotelId
            );
        }

        List<Room> rooms =
                roomRepository
                        .findByHotel_Id(
                                hotelId
                        );

        log.info(
                "Rooms fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                rooms.size()
        );

        return rooms;
    }

    @Override
    public List<Room> getRoomsByHotelAndStatus(
            String hotelId,
            RoomStatus roomStatus
    ) {

        log.debug(
                "Fetching rooms by hotel and status. hotelId={}, status={}",
                hotelId,
                roomStatus
        );

        List<Room> rooms =
                roomRepository
                        .findByHotel_IdAndRoomStatus(
                                hotelId,
                                roomStatus
                        );

        log.info(
                "Rooms fetched successfully by hotel and status. hotelId={}, status={}, count={}",
                hotelId,
                roomStatus,
                rooms.size()
        );

        return rooms;
    }

    @Override
    public List<Room> getRoomsByHotelAndType(
            String hotelId,
            RoomType roomType
    ) {

        log.debug(
                "Fetching rooms by hotel and type. hotelId={}, roomType={}",
                hotelId,
                roomType
        );

        List<Room> rooms =
                roomRepository
                        .findByHotel_IdAndRoomType(
                                hotelId,
                                roomType
                        );

        log.info(
                "Rooms fetched successfully by hotel and type. hotelId={}, roomType={}, count={}",
                hotelId,
                roomType,
                rooms.size()
        );

        return rooms;
    }

    @Override
    @Transactional
    public Room updateRoom(
            String roomId,
            Room newRoom
    ) {

        log.info(
                "Starting room update. roomId={}",
                roomId
        );

        Room existingRoom =
                getRoomById(
                        roomId
                );

        existingRoom.setRoomNumber(
                newRoom.getRoomNumber()
        );

        existingRoom.setRoomType(
                newRoom.getRoomType()
        );

        existingRoom.setPricePerNight(
                newRoom.getPricePerNight()
        );

        existingRoom.setCapacity(
                newRoom.getCapacity()
        );

        existingRoom.setFloorNumber(
                newRoom.getFloorNumber()
        );

        if (newRoom.getRoomStatus() != null) {

            log.debug(
                    "Updating room status during room update. roomId={}, newStatus={}",
                    roomId,
                    newRoom.getRoomStatus()
            );

            existingRoom.setRoomStatus(
                    newRoom.getRoomStatus()
            );
        }

        Room savedRoom =
                roomRepository.save(
                        existingRoom
                );

        log.info(
                "Room updated successfully. roomId={}, roomNumber={}, status={}",
                savedRoom.getRoomId(),
                savedRoom.getRoomNumber(),
                savedRoom.getRoomStatus()
        );

        return savedRoom;
    }

    @Override
    @Transactional
    public Room updateRoomStatus(
            String roomId,
            RoomStatus roomStatus
    ) {

        log.info(
                "Starting room status update. roomId={}, newStatus={}",
                roomId,
                roomStatus
        );

        Room room =
                getRoomById(
                        roomId
                );

        room.setRoomStatus(
                roomStatus
        );

        Room savedRoom =
                roomRepository.save(
                        room
                );

        log.info(
                "Room status updated successfully. roomId={}, status={}",
                savedRoom.getRoomId(),
                savedRoom.getRoomStatus()
        );

        return savedRoom;
    }

    @Override
    public void deleteRoom(
            String roomId
    ) {

        log.info(
                "Starting room deletion. roomId={}",
                roomId
        );

        Room room =
                getRoomById(
                        roomId
                );

        roomRepository.delete(
                room
        );

        log.info(
                "Room deleted successfully. roomId={}",
                roomId
        );
    }
}