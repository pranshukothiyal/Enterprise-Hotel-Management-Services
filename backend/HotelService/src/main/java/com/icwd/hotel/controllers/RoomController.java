package com.icwd.hotel.controllers;

import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;
import com.icwd.hotel.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Create room for a hotel
    @PostMapping("/hotels/{hotelId}")
    public ResponseEntity<Room> createRoom(
            @PathVariable String hotelId,
            @RequestBody Room room
    ) {

        log.info(
                "Received request to create room. hotelId={}",
                hotelId
        );

        Room createdRoom =
                roomService.createRoom(
                        hotelId,
                        room
                );

        log.info(
                "Room created successfully. hotelId={}",
                hotelId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRoom);
    }

    // Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        log.info(
                "Received request to fetch all rooms"
        );

        List<Room> rooms =
                roomService.getAllRooms();

        log.debug(
                "Fetched all rooms successfully. count={}",
                rooms.size()
        );

        return ResponseEntity.ok(
                rooms
        );
    }

    // Get room by ID
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(
            @PathVariable String roomId
    ) {

        log.info(
                "Received request to fetch room. roomId={}",
                roomId
        );

        Room room =
                roomService.getRoomById(
                        roomId
                );

        log.debug(
                "Room fetched successfully. roomId={}",
                roomId
        );

        return ResponseEntity.ok(
                room
        );
    }

    // Get all rooms of one hotel
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch rooms by hotel. hotelId={}",
                hotelId
        );

        List<Room> rooms =
                roomService.getRoomsByHotel(
                        hotelId
                );

        log.debug(
                "Rooms fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                rooms.size()
        );

        return ResponseEntity.ok(
                rooms
        );
    }

    // Get rooms by hotel and status
    @GetMapping("/hotels/{hotelId}/status/{status}")
    public ResponseEntity<List<Room>>
    getRoomsByHotelAndStatus(
            @PathVariable String hotelId,
            @PathVariable RoomStatus status
    ) {

        log.info(
                "Received request to fetch rooms by hotel and status. hotelId={}, status={}",
                hotelId,
                status
        );

        List<Room> rooms =
                roomService.getRoomsByHotelAndStatus(
                        hotelId,
                        status
                );

        log.debug(
                "Rooms fetched successfully by hotel and status. hotelId={}, status={}, count={}",
                hotelId,
                status,
                rooms.size()
        );

        return ResponseEntity.ok(
                rooms
        );
    }

    // Get rooms by hotel and type
    @GetMapping("/hotels/{hotelId}/type/{roomType}")
    public ResponseEntity<List<Room>>
    getRoomsByHotelAndType(
            @PathVariable String hotelId,
            @PathVariable RoomType roomType
    ) {

        log.info(
                "Received request to fetch rooms by hotel and type. hotelId={}, roomType={}",
                hotelId,
                roomType
        );

        List<Room> rooms =
                roomService.getRoomsByHotelAndType(
                        hotelId,
                        roomType
                );

        log.debug(
                "Rooms fetched successfully by hotel and type. hotelId={}, roomType={}, count={}",
                hotelId,
                roomType,
                rooms.size()
        );

        return ResponseEntity.ok(
                rooms
        );
    }

    // Update complete room
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable String roomId,
            @RequestBody Room room
    ) {

        log.info(
                "Received request to update room. roomId={}",
                roomId
        );

        Room updatedRoom =
                roomService.updateRoom(
                        roomId,
                        room
                );

        log.info(
                "Room updated successfully. roomId={}",
                roomId
        );

        return ResponseEntity.ok(
                updatedRoom
        );
    }

    // Update only room status
    @PatchMapping("/{roomId}/status/{status}")
    public ResponseEntity<Room> updateRoomStatus(
            @PathVariable String roomId,
            @PathVariable RoomStatus status
    ) {

        log.info(
                "Received request to update room status. roomId={}, newStatus={}",
                roomId,
                status
        );

        Room updatedRoom =
                roomService.updateRoomStatus(
                        roomId,
                        status
                );

        log.info(
                "Room status updated successfully. roomId={}, newStatus={}",
                roomId,
                status
        );

        return ResponseEntity.ok(
                updatedRoom
        );
    }

    // Delete room
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Map<String, Object>> deleteRoom(
            @PathVariable String roomId
    ) {

        log.info(
                "Received request to delete room. roomId={}",
                roomId
        );

        roomService.deleteRoom(
                roomId
        );

        log.info(
                "Room deleted successfully. roomId={}",
                roomId
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Room deleted successfully",
                        "success",
                        true
                )
        );
    }
}