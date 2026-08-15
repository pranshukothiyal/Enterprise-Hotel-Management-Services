package com.example.AuthService.dto;

public class AssignHotelRequest {

    private String hotelId;


    public AssignHotelRequest() {
    }


    public AssignHotelRequest(
            String hotelId
    ) {

        this.hotelId = hotelId;
    }


    public String getHotelId() {

        return hotelId;
    }


    public void setHotelId(
            String hotelId
    ) {

        this.hotelId = hotelId;
    }
}