package com.akash.oyoclone.repository;

import com.akash.oyoclone.entity.Booking;
import com.akash.oyoclone.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.checkOutDate >= :today ORDER BY b.checkInDate ASC")
    List<Booking> findUpcomingBookingsByUser(@Param("userId") Long userId,
                                              @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.checkOutDate < :today ORDER BY b.checkOutDate DESC")
    List<Booking> findPastBookingsByUser(@Param("userId") Long userId,
                                         @Param("today") LocalDate today);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    boolean existsByRoomIdAndUserIdAndStatus(Long roomId, Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.room.hotel.owner.id = :ownerId")
    List<Booking> findBookingsByHotelOwner(@Param("ownerId") Long ownerId);
}
