package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.RoomRequest;
import com.akash.oyoclone.dto.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse addRoom(Long hotelId, RoomRequest request);

    RoomResponse getRoomById(Long id);

    List<RoomResponse> getRoomsByHotel(Long hotelId);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);
}
