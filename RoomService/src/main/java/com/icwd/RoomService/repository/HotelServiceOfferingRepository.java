package com.icwd.RoomService.repository;

import com.icwd.RoomService.entity.HotelServiceOffering;
import com.icwd.RoomService.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelServiceOfferingRepository
        extends JpaRepository<HotelServiceOffering, String> {

    List<HotelServiceOffering> findByHotelId(
            String hotelId
    );

    List<HotelServiceOffering>
    findByHotelIdAndServiceStatus(
            String hotelId,
            ServiceStatus serviceStatus
    );
}