package com.icwd.rating.RatingService.controllers;

import com.icwd.rating.RatingService.entities.Rating;
import com.icwd.rating.RatingService.services.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    // create rating
    @PostMapping
    public ResponseEntity<Rating> create(
            @RequestBody Rating rating
    ) {

        log.info(
                "Received request to create rating"
        );

        Rating rating1 =
                ratingService.create(
                        rating
                );

        log.info(
                "Rating created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rating1);
    }

    // get all ratings
    @GetMapping
    public ResponseEntity<List<Rating>> getRatings() {

        log.info(
                "Received request to fetch all ratings"
        );

        List<Rating> getallratings =
                ratingService.getRatings();

        log.debug(
                "Fetched all ratings successfully. count={}",
                getallratings.size()
        );

        return ResponseEntity.ok(
                getallratings
        );
    }

    // get all ratings by userId
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getRatingsByUserId(
            @PathVariable String userId
    ) {

        log.info(
                "Received request to fetch ratings by user. userId={}",
                userId
        );

        var ratings =
                ratingService.getRatingByUserId(
                        userId
                );

        log.debug(
                "Ratings fetched successfully for user. userId={}, count={}",
                userId,
                ratings.size()
        );

        return ResponseEntity.ok(
                ratings
        );
    }

    // get all ratings by hotelId
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<Rating>> getRatingsByHotelId(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch ratings by hotel. hotelId={}",
                hotelId
        );

        List<Rating> ratings =
                ratingService.getRatingByHotelId(
                        hotelId
                );

        log.debug(
                "Ratings fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                ratings.size()
        );

        return ResponseEntity.ok(
                ratings
        );
    }
}