package com.akash.oyoclone.repository;

import com.akash.oyoclone.entity.Room;
import com.akash.oyoclone.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.availableRooms >= :required")
    List<Room> findAvailableRooms(@Param("hotelId") Long hotelId,
                                   @Param("required") int required);
}
