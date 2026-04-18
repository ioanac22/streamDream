package com.streamdream.api.service;

import com.streamdream.api.model.Media;
import com.streamdream.api.model.User;
import com.streamdream.api.model.Watchlist;
import com.streamdream.api.repository.MediaRepository;
import com.streamdream.api.repository.UserRepository;
import com.streamdream.api.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            UserRepository userRepository,
                            MediaRepository mediaRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }
    public List<Watchlist> getUserWatchlist(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return watchlistRepository.findByUser_UserId(userId);
    }

    public void addToWatchlist(Integer userId, Integer mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        Watchlist entry = new Watchlist();
        entry.setUser(user);
        entry.setMedia(media);
        entry.setStatus("to_watch");

        watchlistRepository.save(entry);
    }

    public void removeFromWatchlist(Integer watchlistId) {
        if (!watchlistRepository.existsById(watchlistId)) {
            throw new RuntimeException("Watchlist movie not found with id: " + watchlistId);
        }
        watchlistRepository.deleteById(watchlistId);
    }
}