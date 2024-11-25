package com.project.parkingfinder.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkingfinder.dto.ReviewDTO;
import com.project.parkingfinder.model.ParkingLot;
import com.project.parkingfinder.model.Review;
import com.project.parkingfinder.model.User;
import com.project.parkingfinder.service.ParkingLotService;
import com.project.parkingfinder.service.ReviewService;
import com.project.parkingfinder.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ParkingLotService parkingLotService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, ParkingLotService parkingLotService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.parkingLotService = parkingLotService;
    }

    @GetMapping("/parking-lots/{parkingLotId}")
    public ResponseEntity<List<Review>> getReviewsByParkingLotId(@PathVariable Long parkingLotId) {
        List<Review> reviews = reviewService.findByParkingLotId(parkingLotId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.findById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

   @PostMapping
public ResponseEntity<Review> createReview(@RequestBody @Valid ReviewDTO.CreateReviewRequest request) {
    Review review = new Review();
    User user = userService.getUser(request.getUserId());
    ParkingLot parkingLot = parkingLotService.getById(request.getParkingLotId());
    review.setUser(user);
    review.setParkingLot(parkingLot);
    review.setRating(request.getRating());
    review.setComment(request.getComment());
    review.setCreated(LocalDateTime.now());
    review.setUpdated(LocalDateTime.now());

    Review newReview = reviewService.save(review);
    return new ResponseEntity<>(newReview, HttpStatus.CREATED);
}


    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
        Review updatedReview = reviewService.update(id, review);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
        return new ResponseEntity<>("Review with ID " + id + " has been deleted", HttpStatus.OK);
    }
}
