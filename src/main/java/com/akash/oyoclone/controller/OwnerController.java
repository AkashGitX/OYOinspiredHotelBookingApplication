package com.akash.oyoclone.controller;

import com.akash.oyoclone.dto.*;
import com.akash.oyoclone.service.HotelService;
import com.akash.oyoclone.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('HOTEL_OWNER')")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final HotelService hotelService;
    private final RoomService roomService;

    @GetMapping("/hotels")
    public ResponseEntity<ApiResponse<List<HotelResponse>>> getOwnerHotels() {
        List<HotelResponse> hotels = hotelService.getOwnerHotels();
        return ResponseEntity.ok(ApiResponse.success("Owner hotels fetched", hotels));
    }

    @PostMapping(value = "/hotels", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HotelResponse>> createHotel(
            @Valid @ModelAttribute HotelRequest request,
            @RequestPart("image") MultipartFile image) {
        HotelResponse hotel = hotelService.createHotel(request, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", hotel));
    }

    @PutMapping("/hotels/{id}")
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request) {
        HotelResponse hotel = hotelService.updateHotel(id, request);
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", hotel));
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok(ApiResponse.success("Hotel deleted successfully"));
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse room = roomService.addRoom(hotelId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room added successfully", room));
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHotel(
            @PathVariable Long hotelId) {
        List<RoomResponse> rooms = roomService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(ApiResponse.success("Rooms fetched successfully", rooms));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse room = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", room));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }
}
