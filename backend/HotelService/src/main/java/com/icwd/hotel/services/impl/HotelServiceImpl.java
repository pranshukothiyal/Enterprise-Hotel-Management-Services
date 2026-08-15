package com.icwd.hotel.services.impl;

import com.icwd.hotel.entities.Hotel;
import com.icwd.hotel.exception.ResourceNotFoundException;
import com.icwd.hotel.repository.HotelRepository;
import com.icwd.hotel.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public Hotel create(Hotel hotel) {

        log.info(
                "Starting hotel creation"
        );

        String hotelId =
                UUID.randomUUID().toString();

        hotel.setId(
                hotelId
        );

        log.debug(
                "Generated hotel ID. hotelId={}",
                hotelId
        );

        Hotel savedHotel =
                hotelRepository.save(
                        hotel
                );

        log.info(
                "Hotel created successfully. hotelId={}",
                savedHotel.getId()
        );

        return savedHotel;
    }

    @Override
    public List<Hotel> getAll() {

        log.debug(
                "Fetching all hotels from repository"
        );

        List<Hotel> hotels =
                hotelRepository.findAll();

        log.info(
                "Hotels fetched successfully. count={}",
                hotels.size()
        );

        return hotels;
    }

    @Override
    public Hotel get(String id) {

        log.debug(
                "Fetching hotel by ID. hotelId={}",
                id
        );

        return hotelRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Hotel not found. hotelId={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Hotel with given id not found!!!"
                    );
                });
    }
}