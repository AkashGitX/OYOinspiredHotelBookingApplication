package com.akash.oyoclone.controller;

import com.akash.oyoclone.dto.ApiResponse;
import com.akash.oyoclone.dto.ReviewRequest;
import com.akash.oyoclone.dto.ReviewResponse;
import com.akash.oyoclone.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.addReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", review));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getHotelReviews(
            @PathVariable Long hotelId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByHotel(hotelId);
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", reviews));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews() {
        List<ReviewResponse> reviews = reviewService.getUserReviews();
        return ResponseEntity.ok(ApiResponse.success("Your reviews fetched", reviews));
    }
}
