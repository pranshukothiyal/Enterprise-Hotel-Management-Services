package com.icwd.RoomService.service;

import com.icwd.RoomService.entity.HotelServiceOffering;
import com.icwd.RoomService.entity.ServiceStatus;
import com.icwd.RoomService.exception.ResourceNotFoundException;
import com.icwd.RoomService.repository.HotelServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelServiceOfferingService {

    private final HotelServiceOfferingRepository repository;

    public HotelServiceOffering create(
            HotelServiceOffering offering
    ) {
        if (offering.getServiceId() == null
                || offering.getServiceId().isBlank()) {

            offering.setServiceId(
                    "SVC-" + UUID.randomUUID()
            );
        }

        if (offering.getServiceStatus() == null) {
            offering.setServiceStatus(
                    ServiceStatus.AVAILABLE
            );
        }

        return repository.save(offering);
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public HotelServiceOffering getById(
            String serviceId
    ) {
        return repository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel service not found: "
                                        + serviceId
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering> getByHotelId(
            String hotelId
    ) {
        return repository.findByHotelId(hotelId);
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering>
    getAvailableByHotelId(
            String hotelId
    ) {
        return repository
                .findByHotelIdAndServiceStatus(
                        hotelId,
                        ServiceStatus.AVAILABLE
                );
    }

    public HotelServiceOffering update(
            String serviceId,
            HotelServiceOffering updated
    ) {
        HotelServiceOffering existing =
                getById(serviceId);

        existing.setHotelId(updated.getHotelId());
        existing.setServiceName(
                updated.getServiceName()
        );
        existing.setDescription(
                updated.getDescription()
        );
        existing.setPrice(updated.getPrice());

        if (updated.getServiceStatus() != null) {
            existing.setServiceStatus(
                    updated.getServiceStatus()
            );
        }

        return repository.save(existing);
    }

    public HotelServiceOffering updateStatus(
            String serviceId,
            ServiceStatus status
    ) {
        HotelServiceOffering offering =
                getById(serviceId);

        offering.setServiceStatus(status);

        return repository.save(offering);
    }

    public void delete(String serviceId) {
        HotelServiceOffering offering =
                getById(serviceId);

        repository.delete(offering);
    }
}