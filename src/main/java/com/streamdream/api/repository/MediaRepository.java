package com.streamdream.api.repository;

import com.streamdream.api.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByGenreContaining(String genre);
    boolean existsByTitleAndReleaseYear(String title, Integer releaseYear);
}