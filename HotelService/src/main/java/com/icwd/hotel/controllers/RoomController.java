package com.icwd.hotel.controllers;

import com.icwd.hotel.entities.Room;
import com.icwd.hotel.entities.RoomStatus;
import com.icwd.hotel.entities.RoomType;
import com.icwd.hotel.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        roomService.createRoom(
                                hotelId,
                                room
                        )
                );
    }

    // Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        return ResponseEntity.ok(
                roomService.getAllRooms()
        );
    }

    // Get room by ID
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(
            @PathVariable String roomId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomById(roomId)
        );
    }

    // Get all rooms of one hotel
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(
            @PathVariable String hotelId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomsByHotel(hotelId)
        );
    }

    // Get rooms by hotel and status
    @GetMapping("/hotels/{hotelId}/status/{status}")
    public ResponseEntity<List<Room>>
    getRoomsByHotelAndStatus(
            @PathVariable String hotelId,
            @PathVariable RoomStatus status
    ) {

        return ResponseEntity.ok(
                roomService.getRoomsByHotelAndStatus(
                        hotelId,
                        status
                )
        );
    }

    // Get rooms by hotel and type
    @GetMapping("/hotels/{hotelId}/type/{roomType}")
    public ResponseEntity<List<Room>>
    getRoomsByHotelAndType(
            @PathVariable String hotelId,
            @PathVariable RoomType roomType
    ) {

        return ResponseEntity.ok(
                roomService.getRoomsByHotelAndType(
                        hotelId,
                        roomType
                )
        );
    }

    // Update complete room
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable String roomId,
            @RequestBody Room room
    ) {

        return ResponseEntity.ok(
                roomService.updateRoom(roomId, room)
        );
    }

    // Update only room status
    @PatchMapping("/{roomId}/status/{status}")
    public ResponseEntity<Room> updateRoomStatus(
            @PathVariable String roomId,
            @PathVariable RoomStatus status
    ) {

        return ResponseEntity.ok(
                roomService.updateRoomStatus(
                        roomId,
                        status
                )
        );
    }

    // Delete room
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Map<String, Object>> deleteRoom(
            @PathVariable String roomId
    ) {

        roomService.deleteRoom(roomId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Room deleted successfully",
                        "success", true
                )
        );
    }
}