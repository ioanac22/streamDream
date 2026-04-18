package com.streamdream.api.controller;

import com.streamdream.api.model.Review;
import com.streamdream.api.repository.ReviewRepository;
import com.streamdream.api.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable Integer userId) {
        try {
            List<Review> reviews = reviewRepository.findByUser_UserId(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/add")
    public ResponseEntity<String> addReview(@RequestParam Integer userId,
                                            @RequestParam Integer mediaId,
                                            @RequestParam int rating,
                                            @RequestParam(required = false) String comment) {
        reviewService.addReviewAndMarkWatched(userId, mediaId, rating, comment);
        return ResponseEntity.ok("Movie reviewed!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id) {
        if(reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
            ResponseEntity.ok("Deleted!");
        }

        return ResponseEntity.ok("Not found");
    }
}