package com.icwd.rating.RatingService.services;

import com.icwd.rating.RatingService.entities.Rating;

import java.util.List;

public interface RatingService {
//    create
    Rating create(Rating rating);

//    get all ratings
    List<Rating> getRatings();

//   get all by UserId ( getting user wise rating using userId)
List<Rating> getRatingByUserId(String userId);
//    get all by hotel
    List<Rating> getRatingByHotelId(String hotelId);
}
