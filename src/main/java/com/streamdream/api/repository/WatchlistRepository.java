package com.streamdream.api.repository;

import com.streamdream.api.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {

    Optional<Watchlist> findByUser_UserIdAndMedia_MediaId(Integer userId, Integer mediaId);
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END " +
            "FROM Watchlist w WHERE w.user.userId = :userId AND w.media.id = :mediaId")
    boolean existsByUserIdAndMediaId(@Param("userId") Integer userId, @Param("mediaId") Integer mediaId);
    List<Watchlist> findByUser_UserId(Integer userId);
}