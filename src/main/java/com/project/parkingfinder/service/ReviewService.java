package com.project.parkingfinder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.parkingfinder.exception.ResourceNotFoundException;
import com.project.parkingfinder.model.Review;
import com.project.parkingfinder.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(Long id) {
        Review review = reviewRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy review với id: " + id));

        return review;
    }

    public List<Review> findByParkingLotId(Long parkingLotId) {
        return reviewRepository.findByParkingLotId(parkingLotId);
                
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Review update(Long id, Review review) {
        review.setId(id);
        return reviewRepository.save(review);
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}
