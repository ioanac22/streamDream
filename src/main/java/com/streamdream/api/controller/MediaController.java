package com.streamdream.api.controller;

import com.streamdream.api.model.Media;
import com.streamdream.api.model.MediaDTO;
import com.streamdream.api.service.MediaService;
import com.streamdream.api.service.PosterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController 
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaController {

    private final MediaService mediaService;
    private final PosterService posterService;

    public MediaController(MediaService mediaService, PosterService posterService) {

        this.mediaService = mediaService;
        this.posterService = posterService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Media>> getAllMedia() {
        List<Media> mediaList = mediaService.getAllMedia();
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/search")
    public List<Media> getMediaByGenre(@RequestParam String genre) {
        return mediaService.getMoviesByGenre(genre);
    }

    @GetMapping("/fetch-external")
    public ResponseEntity<MediaDTO> getExternalMovie(@RequestParam String title) {
        MediaDTO movie = posterService.fetchMovieFromApi(title);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMedia(@RequestBody MediaDTO mediaDTO) {
        try {
            Media savedMedia = mediaService.saveMedia(mediaDTO);
            return ResponseEntity.ok(savedMedia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable Integer id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok("Movie with ID " + id + " was deleted!");
    }

}