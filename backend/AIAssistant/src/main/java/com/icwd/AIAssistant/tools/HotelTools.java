package com.icwd.AIAssistant.tools;

import com.icwd.AIAssistant.client.BookingClient;
import com.icwd.AIAssistant.client.HotelClient;
import com.icwd.AIAssistant.client.RatingClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HotelTools {

    private final HotelClient hotelClient;
    private final RatingClient ratingClient;
    private final BookingClient bookingClient;

    public HotelTools(
            HotelClient hotelClient,
            RatingClient ratingClient,
            BookingClient bookingClient
    ) {
        this.hotelClient = hotelClient;
        this.ratingClient = ratingClient;
        this.bookingClient = bookingClient;
    }

    @Tool(
            name = "getCurrentHotels",
            description = """
                    Get all current hotels from HOTEL-SERVICE.
                    Use this tool whenever the user asks about available hotels,
                    hotel names, locations, facilities or recommendations.
                    Never invent hotel information.
                    """
    )
    public List<Map<String, Object>> getCurrentHotels() {
        try {
            return hotelClient.getAllHotels();
        } catch (Exception e) {
            return List.of(Map.of("error", "Could not fetch hotels: " + e.getMessage()));
        }
    }

    @Tool(
            name = "getCurrentRooms",
            description = """
                    Get all current rooms from HOTEL-SERVICE.
                    Use this tool when the user asks about room availability,
                    room type, room status, price, capacity or recommendations.
                    Never invent room IDs, prices or availability.
                    """
    )
    public List<Map<String, Object>> getCurrentRooms() {
        try {
            return hotelClient.getAllRooms();
        } catch (Exception e) {
            return List.of(Map.of("error", "Could not fetch rooms: " + e.getMessage()));
        }
    }

    @Tool(
            name = "searchHotelsByLocation",
            description = """
                    Search hotels using a city or location.
                    Use this tool when the user asks for hotels in a particular
                    city, area or destination.
                    Pass only the location provided by the user.
                    """
    )
    public List<Map<String, Object>> searchHotelsByLocation(String location) {
        if (location == null || location.isBlank()) {
            return List.of(Map.of("error", "Location must not be empty. Ask the user to specify a city."));
        }

        try {
            return hotelClient.getHotelsByLocation(location.trim());
        } catch (feign.FeignException.NotFound e) {
            return Collections.emptyList(); // LLM sees an empty list and knows no hotels exist here
        } catch (Exception e) {
            return List.of(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    @Tool(
            name = "getHotelDetails",
            description = """
                    Get complete information about a hotel using its hotel ID.
                    Use this tool when the user asks about a particular hotel's
                    address, description, facilities or other details.
                    Never create or guess a hotel ID.
                    """
    )
    public Map<String, Object> getHotelDetails(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) {
            return Map.of("error", "Hotel ID must not be empty. Ask the user for clarification.");
        }

        try {
            return hotelClient.getHotelById(hotelId);
        } catch (feign.FeignException.NotFound e) {
            return Map.of("error", "No hotel found with ID: " + hotelId);
        } catch (Exception e) {
            return Map.of("error", "Could not fetch hotel details: " + e.getMessage());
        }
    }

    @Tool(
            name = "getRoomsByHotel",
            description = """
                    Get all rooms belonging to a particular hotel.
                    Use this tool when the user asks about rooms, prices,
                    room types or capacity for a specific hotel.
                    """
    )
    public List<Map<String, Object>> getRoomsByHotel(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) {
            return List.of(Map.of("error", "Hotel ID is missing."));
        }

        try {
            return hotelClient.getRoomsByHotelId(hotelId);
        } catch (feign.FeignException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return List.of(Map.of("error", "Could not fetch rooms: " + e.getMessage()));
        }
    }

    @Tool(
            name = "checkRoomAvailability",
            description = """
                    Check whether a room is available between a check-in date
                    and a check-out date.

                    Dates must use YYYY-MM-DD format.
                    Use this tool before recommending or booking a room.
                    Never assume that a room is available.
                    """
    )
    public Map<String, Object> checkRoomAvailability(
            String roomId,
            String checkInDate,
            String checkOutDate
    ) {
        if (roomId == null || roomId.isBlank()) {
            return Map.of("error", "Room ID must not be empty.");
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            if (!checkOut.isAfter(checkIn)) {
                return Map.of("error", "Check-out date must be after check-in date.");
            }

            return bookingClient.checkAvailability(roomId, checkIn, checkOut);

        } catch (DateTimeParseException e) {
            return Map.of("error", "Invalid date format. Please format dates as YYYY-MM-DD.");
        } catch (Exception e) {
            return Map.of("error", "Could not check availability: " + e.getMessage());
        }
    }

    @Tool(
            name = "getHotelRatings",
            description = """
                    Get ratings and customer reviews for a particular hotel.
                    Use this tool when the user asks whether a hotel is good,
                    asks for reviews, ratings or customer feedback.
                    Never invent ratings or reviews.
                    """
    )
    public List<Map<String, Object>> getHotelRatings(String hotelId) {
        if (hotelId == null || hotelId.isBlank()) {
            return List.of(Map.of("error", "Hotel ID is missing."));
        }

        try {
            return ratingClient.getRatingsByHotelId(hotelId);
        } catch (feign.FeignException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return List.of(Map.of("error", "Could not fetch ratings: " + e.getMessage()));
        }
    }

    @Tool(
            name = "getUserBookings",
            description = """
                    Get booking history for a particular user.
                    Use this only when the authenticated user asks about
                    their own bookings.

                    Do not use this tool to access another customer's bookings.
                    """
    )
    public List<Map<String, Object>> getUserBookings(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of(Map.of("error", "User ID is missing."));
        }

        try {
            return bookingClient.getBookingsByUserId(userId);
        } catch (feign.FeignException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return List.of(Map.of("error", "Could not fetch user bookings: " + e.getMessage()));
        }
    }


}