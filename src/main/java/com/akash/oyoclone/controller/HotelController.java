package com.akash.oyoclone.controller;

import com.akash.oyoclone.dto.ApiResponse;
import com.akash.oyoclone.dto.HotelResponse;
import com.akash.oyoclone.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelResponse>>> getAllHotels(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city) {

        List<HotelResponse> hotels;

        if (search != null && !search.isBlank()) {
            hotels = hotelService.searchHotels(search);
        }
        else if (city != null && !city.isBlank()) {
            hotels = hotelService.searchByCity(city);
        }
        else {
            hotels = hotelService.getAllApprovedHotels();
        }

        return ResponseEntity.ok(
                ApiResponse.success("Hotels fetched successfully", hotels));
    }

    // ✅ PUBLIC ENDPOINT (Very Important for hotel details page)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelResponse>> getHotelById(
            @PathVariable Long id) {

        HotelResponse hotel = hotelService.getHotelById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Hotel fetched successfully", hotel));
    }
}