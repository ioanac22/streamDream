package com.streamdream.api.service;

import com.streamdream.api.model.Media;
import com.streamdream.api.model.Review;
import com.streamdream.api.model.User;
import com.streamdream.api.repository.ReviewRepository;
import com.streamdream.api.repository.WatchlistRepository;
import com.streamdream.api.repository.UserRepository;
import com.streamdream.api.repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         WatchlistRepository watchlistRepository,
                         UserRepository userRepository,
                         MediaRepository mediaRepository) {
        this.reviewRepository = reviewRepository;
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public void addReviewAndMarkWatched(Integer userId, Integer mediaId, int rating, String comment) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        Review review = new Review();
        review.setUser(user);
        review.setMedia(media);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        try {
            watchlistRepository.findByUser_UserIdAndMedia_MediaId(userId, mediaId)
                    .ifPresent(watchlist -> {
                        watchlistRepository.delete(watchlist);
                        System.out.println("Removed from watchlist!");
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteById(Integer id) {
        reviewRepository.deleteById(id);
    }
}