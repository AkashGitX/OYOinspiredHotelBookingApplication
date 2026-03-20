package com.akash.oyoclone.repository;

import com.akash.oyoclone.entity.Hotel;
import com.akash.oyoclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByApprovedTrue();

    List<Hotel> findByCityContainingIgnoreCaseAndApprovedTrue(String city);

    List<Hotel> findByOwner(User owner);

    List<Hotel> findByOwnerId(Long ownerId);

    @Query("SELECT h FROM Hotel h WHERE h.approved = true AND " +
           "(LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Hotel> searchHotels(@Param("query") String query);
}
