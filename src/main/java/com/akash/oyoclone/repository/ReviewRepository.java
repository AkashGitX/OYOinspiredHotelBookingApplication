package com.akash.oyoclone.repository;

import com.akash.oyoclone.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHotelId(Long hotelId);

    List<Review> findByUserId(Long userId);

    boolean existsByHotelIdAndUserId(Long hotelId, Long userId);

    Optional<Review> findByHotelIdAndUserId(Long hotelId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Double findAverageRatingByHotelId(@Param("hotelId") Long hotelId);

    long countByHotelId(Long hotelId);
}
