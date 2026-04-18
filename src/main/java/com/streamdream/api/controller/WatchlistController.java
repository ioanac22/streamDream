package com.streamdream.api.controller;

import com.streamdream.api.model.Watchlist;
import com.streamdream.api.repository.WatchlistRepository;
import com.streamdream.api.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin("*")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final WatchlistRepository watchlistRepository;

    public WatchlistController(WatchlistService watchlistService, WatchlistRepository watchlistRepository) {
        this.watchlistService = watchlistService;
        this.watchlistRepository = watchlistRepository;
    }
    @GetMapping("/count")
    public long getCount() {
        return watchlistRepository.count();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Watchlist>> getWatchlist(@PathVariable Integer userId) {
        return ResponseEntity.ok(watchlistService.getUserWatchlist(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestParam Integer userId, @RequestParam Integer mediaId) {
        if (watchlistRepository.existsByUserIdAndMediaId(userId, mediaId)) {
            return ResponseEntity.badRequest().body("Already in your list!");
        }
        watchlistService.addToWatchlist(userId, mediaId);
        return ResponseEntity.ok("Added to watchlist!");
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> remove(@PathVariable Integer id) {
        watchlistService.removeFromWatchlist(id);
        return ResponseEntity.ok("Movie removed from watchlist!");
    }
}