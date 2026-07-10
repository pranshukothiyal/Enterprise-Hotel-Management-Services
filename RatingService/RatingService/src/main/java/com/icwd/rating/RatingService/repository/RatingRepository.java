package com.icwd.rating.RatingService.repository;

import com.icwd.rating.RatingService.entities.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating,String> {
//    derived method
//    for finding all rating of user
    List<Rating> findByUserId(String userId);
//    for finding all rating of hotel
    List<Rating> findByHotelId(String hotelId);
}
