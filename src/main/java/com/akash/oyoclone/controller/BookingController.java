package com.akash.oyoclone.controller;

import com.akash.oyoclone.dto.ApiResponse;
import com.akash.oyoclone.dto.BookingRequest;
import com.akash.oyoclone.dto.BookingResponse;
import com.akash.oyoclone.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings() {
        List<BookingResponse> bookings = bookingService.getUserBookings();
        return ResponseEntity.ok(ApiResponse.success("Bookings fetched successfully", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success("Booking fetched successfully", booking));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingBookings() {
        List<BookingResponse> bookings = bookingService.getUpcomingBookings();
        return ResponseEntity.ok(ApiResponse.success("Upcoming bookings fetched", bookings));
    }

    @GetMapping("/past")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getPastBookings() {
        List<BookingResponse> bookings = bookingService.getPastBookings();
        return ResponseEntity.ok(ApiResponse.success("Past bookings fetched", bookings));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }
}
