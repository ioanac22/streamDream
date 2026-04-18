package com.streamdream.api.service;

import com.streamdream.api.model.Media;
import com.streamdream.api.model.MediaDTO;
import com.streamdream.api.repository.MediaRepository;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<Media> getMoviesByGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            return mediaRepository.findAll();
        }
        return mediaRepository.findByGenreContaining(genre);
    }
    public Media saveMedia(@Nonnull MediaDTO dto){
        if(mediaRepository.existsByTitleAndReleaseYear(dto.getTitle(), dto.getReleaseYear())){
            throw new RuntimeException("Movie already added!");
        }
        Media media = new Media();
        media.setTitle(dto.getTitle());
        media.setGenre(dto.getGenre());
        media.setReleaseYear(dto.getReleaseYear());
        media.setPosterUrl(dto.getPosterUrl());
        media.setDescription(dto.getDescription());
        return mediaRepository.save(media);
    }
    public void deleteMedia(Integer id){
        mediaRepository.deleteById(id);
    }

}