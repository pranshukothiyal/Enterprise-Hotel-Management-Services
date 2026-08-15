package com.icwd.RoomService.service;

import com.icwd.RoomService.entity.HotelServiceOffering;
import com.icwd.RoomService.entity.ServiceStatus;
import com.icwd.RoomService.exception.ResourceNotFoundException;
import com.icwd.RoomService.repository.HotelServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HotelServiceOfferingService {

    private final HotelServiceOfferingRepository repository;

    public HotelServiceOffering create(
            HotelServiceOffering offering
    ) {

        log.info(
                "Starting hotel service offering creation"
        );

        if (offering.getServiceId() == null
                || offering.getServiceId().isBlank()) {

            offering.setServiceId(
                    "SVC-" + UUID.randomUUID()
            );

            log.debug(
                    "Generated hotel service offering ID. serviceId={}",
                    offering.getServiceId()
            );
        }

        if (offering.getServiceStatus() == null) {

            offering.setServiceStatus(
                    ServiceStatus.AVAILABLE
            );

            log.debug(
                    "Default hotel service status set to AVAILABLE. serviceId={}",
                    offering.getServiceId()
            );
        }

        HotelServiceOffering savedOffering =
                repository.save(
                        offering
                );

        log.info(
                "Hotel service offering created successfully. serviceId={}",
                savedOffering.getServiceId()
        );

        return savedOffering;
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering> getAll() {

        log.debug(
                "Fetching all hotel service offerings from repository"
        );

        List<HotelServiceOffering> offerings =
                repository.findAll();

        log.info(
                "Hotel service offerings fetched successfully. count={}",
                offerings.size()
        );

        return offerings;
    }

    @Transactional(readOnly = true)
    public HotelServiceOffering getById(
            String serviceId
    ) {

        log.debug(
                "Fetching hotel service offering by ID. serviceId={}",
                serviceId
        );

        return repository
                .findById(serviceId)
                .orElseThrow(() -> {

                    log.warn(
                            "Hotel service offering not found. serviceId={}",
                            serviceId
                    );

                    return new ResourceNotFoundException(
                            "Hotel service not found: "
                                    + serviceId
                    );
                });
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering> getByHotelId(
            String hotelId
    ) {

        log.debug(
                "Fetching hotel service offerings by hotel. hotelId={}",
                hotelId
        );

        List<HotelServiceOffering> offerings =
                repository.findByHotelId(
                        hotelId
                );

        log.info(
                "Hotel service offerings fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                offerings.size()
        );

        return offerings;
    }

    @Transactional(readOnly = true)
    public List<HotelServiceOffering> getAvailableByHotelId(
            String hotelId
    ) {

        log.debug(
                "Fetching available hotel service offerings. hotelId={}",
                hotelId
        );

        List<HotelServiceOffering> offerings =
                repository
                        .findByHotelIdAndServiceStatus(
                                hotelId,
                                ServiceStatus.AVAILABLE
                        );

        log.info(
                "Available hotel service offerings fetched successfully. hotelId={}, count={}",
                hotelId,
                offerings.size()
        );

        return offerings;
    }

    public HotelServiceOffering update(
            String serviceId,
            HotelServiceOffering updated
    ) {

        log.info(
                "Starting hotel service offering update. serviceId={}",
                serviceId
        );

        HotelServiceOffering existing =
                getById(
                        serviceId
                );

        existing.setHotelId(
                updated.getHotelId()
        );

        existing.setServiceName(
                updated.getServiceName()
        );

        existing.setDescription(
                updated.getDescription()
        );

        existing.setPrice(
                updated.getPrice()
        );

        if (updated.getServiceStatus() != null) {

            existing.setServiceStatus(
                    updated.getServiceStatus()
            );

            log.debug(
                    "Hotel service offering status updated during update. serviceId={}, status={}",
                    serviceId,
                    updated.getServiceStatus()
            );
        }

        HotelServiceOffering savedOffering =
                repository.save(
                        existing
                );

        log.info(
                "Hotel service offering updated successfully. serviceId={}",
                serviceId
        );

        return savedOffering;
    }

    public HotelServiceOffering updateStatus(
            String serviceId,
            ServiceStatus status
    ) {

        log.info(
                "Starting hotel service offering status update. serviceId={}, status={}",
                serviceId,
                status
        );

        HotelServiceOffering offering =
                getById(
                        serviceId
                );

        offering.setServiceStatus(
                status
        );

        HotelServiceOffering savedOffering =
                repository.save(
                        offering
                );

        log.info(
                "Hotel service offering status updated successfully. serviceId={}, status={}",
                serviceId,
                status
        );

        return savedOffering;
    }

    public void delete(
            String serviceId
    ) {

        log.info(
                "Starting hotel service offering deletion. serviceId={}",
                serviceId
        );

        HotelServiceOffering offering =
                getById(
                        serviceId
                );

        repository.delete(
                offering
        );

        log.info(
                "Hotel service offering deleted successfully. serviceId={}",
                serviceId
        );
    }
}