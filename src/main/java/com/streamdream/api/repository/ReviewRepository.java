package com.streamdream.api.repository;

import com.streamdream.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUser_UserId(Integer userId);
}
