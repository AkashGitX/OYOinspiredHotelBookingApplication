package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.dto.RoomRequest;
import com.akash.oyoclone.dto.RoomResponse;
import com.akash.oyoclone.entity.Hotel;
import com.akash.oyoclone.entity.Room;
import com.akash.oyoclone.entity.User;
import com.akash.oyoclone.exception.ResourceNotFoundException;
import com.akash.oyoclone.exception.UnauthorizedException;
import com.akash.oyoclone.repository.HotelRepository;
import com.akash.oyoclone.repository.RoomRepository;
import com.akash.oyoclone.service.RoomService;
import com.akash.oyoclone.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public RoomResponse addRoom(Long hotelId, RoomRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));

        User currentUser = securityUtils.getCurrentUser();
        if (!hotel.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only add rooms to your own hotels");
        }

        Room room = Room.builder()
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .totalRooms(request.getTotalRooms())
                .availableRooms(request.getTotalRooms())
                .capacity(request.getCapacity())
                .hotel(hotel)
                .build();

        Room saved = roomRepository.save(room);
        log.info("Room added: {} to hotel {}", saved.getRoomType(), hotel.getName());

        return mapToRoomResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        return mapToRoomResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        User currentUser = securityUtils.getCurrentUser();
        if (!room.getHotel().getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update rooms in your own hotels");
        }

        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setTotalRooms(request.getTotalRooms());
        room.setCapacity(request.getCapacity());

        Room updated = roomRepository.save(room);
        return mapToRoomResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        User currentUser = securityUtils.getCurrentUser();
        if (!room.getHotel().getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete rooms in your own hotels");
        }

        roomRepository.delete(room);
    }

    private RoomResponse mapToRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .totalRooms(room.getTotalRooms())
                .availableRooms(room.getAvailableRooms())
                .capacity(room.getCapacity())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .build();
    }
}
