package com.icwd.rating.RatingService.services.impl;

import com.icwd.rating.RatingService.entities.Rating;
import com.icwd.rating.RatingService.repository.RatingRepository;
import com.icwd.rating.RatingService.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    @Override
    public Rating create(
            Rating rating
    ) {

        log.info(
                "Starting rating creation"
        );

        Rating savedRating =
                repository.save(
                        rating
                );

        log.info(
                "Rating created successfully"
        );

        return savedRating;
    }

    @Override
    public List<Rating> getRatings() {

        log.debug(
                "Fetching all ratings from repository"
        );

        List<Rating> ratings =
                repository.findAll();

        log.info(
                "Ratings fetched successfully. count={}",
                ratings.size()
        );

        return ratings;
    }

    @Override
    public List<Rating> getRatingByUserId(
            String userId
    ) {

        log.debug(
                "Fetching ratings by user. userId={}",
                userId
        );

        List<Rating> ratings =
                repository.findByUserId(
                        userId
                );

        log.info(
                "Ratings fetched successfully for user. userId={}, count={}",
                userId,
                ratings.size()
        );

        return ratings;
    }

    @Override
    public List<Rating> getRatingByHotelId(
            String hotelId
    ) {

        log.debug(
                "Fetching ratings by hotel. hotelId={}",
                hotelId
        );

        List<Rating> ratings =
                repository.findByHotelId(
                        hotelId
                );

        log.info(
                "Ratings fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                ratings.size()
        );

        return ratings;
    }
}