package com.icwd.hotel.services;

import com.icwd.hotel.entities.Hotel;

import java.util.List;

public interface HotelService {
    //    create
    Hotel create(Hotel hotel);
    //    get all
    List<Hotel> getAll();
    //    get single
    Hotel get(String id);
}