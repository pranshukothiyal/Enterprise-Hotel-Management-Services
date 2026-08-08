package com.icwd.rating.RatingService.controllers;

import com.icwd.rating.RatingService.entities.Rating;
import com.icwd.rating.RatingService.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;
//    create rating
    @PostMapping
    public ResponseEntity<Rating> create(@RequestBody Rating rating){
        Rating rating1=ratingService.create(rating);
       return ResponseEntity.status(HttpStatus.CREATED).body(rating1);
    }
//    getall
    @GetMapping
    public ResponseEntity<List<Rating>> getRatings(){
    List<Rating> getallratings =ratingService.getRatings();
   return ResponseEntity.ok(getallratings);
    }
//    get all rating by userId
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getRatingsByUserId(@PathVariable String userId){
        return ResponseEntity.ok(ratingService.getRatingByUserId(userId));
    }


//    get all rating by hotelId
    @GetMapping("/hotels/{hotelId}")
    public ResponseEntity<List<Rating>> getRatingsByHotelId(@PathVariable String hotelId){
        return ResponseEntity.ok(ratingService.getRatingByHotelId(hotelId));
    }
}
