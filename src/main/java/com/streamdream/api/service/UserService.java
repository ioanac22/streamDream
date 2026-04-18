package com.streamdream.api.service;

import com.streamdream.api.model.Login;
import com.streamdream.api.model.Register;
import com.streamdream.api.model.User;
import com.streamdream.api.model.Review;
import com.streamdream.api.model.Watchlist;
import com.streamdream.api.repository.UserRepository;
import com.streamdream.api.repository.ReviewRepository;
import com.streamdream.api.repository.WatchlistRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final WatchlistRepository watchlistRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ReviewRepository reviewRepository,
                       WatchlistRepository watchlistRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.watchlistRepository = watchlistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(Register register){
        if(userRepository.existsByUsername(register.getUsername())){
            throw new RuntimeException("Username already exists!");
        }
        if(userRepository.existsByEmail(register.getEmail())){
            throw  new RuntimeException("Email already registered!");
        }
        User user = new User();
        user.setUsername(register.getUsername());
        user.setEmail(register.getEmail());
        user.setPasswordHash(passwordEncoder.encode(register.getPassword()));
        user.setRole("user");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User loginUser(Login login){
        User user = userRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(login.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password!");
        }
        if ("banned".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Account is banned!");
        }
        return user;
    }

    public User updateAvatar(Integer userId, String base64Image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarUrl(base64Image);
        return userRepository.save(user);
    }

    public User updateBio(Integer userId, String bio) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBio(bio);
        return userRepository.save(user);
    }

    public User updateUsername(Integer userId, String newUsername) {
        if (newUsername == null || newUsername.trim().length() < 3) {
            throw new RuntimeException("Username too short!");
        }

        var existingUserOpt = userRepository.findByUsername(newUsername);
        if (existingUserOpt.isPresent()) {
            if (!existingUserOpt.get().getUserId().equals(userId)) {
                throw new RuntimeException("Username already taken!");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        List<Review> userReviews = reviewRepository.findByUser_UserId(userId);
        reviewRepository.deleteAll(userReviews);
        List<Watchlist> userWatchlist = watchlistRepository.findByUser_UserId(userId);
        watchlistRepository.deleteAll(userWatchlist);
        userRepository.deleteById(userId);
    }

    public void banUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("banned");
        userRepository.save(user);
    }
    public void unbanUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("active");
        userRepository.save(user);
    }
}