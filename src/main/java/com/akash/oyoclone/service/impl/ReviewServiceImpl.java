package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.dto.ReviewRequest;
import com.akash.oyoclone.dto.ReviewResponse;
import com.akash.oyoclone.entity.*;
import com.akash.oyoclone.exception.BadRequestException;
import com.akash.oyoclone.exception.ResourceNotFoundException;
import com.akash.oyoclone.repository.BookingRepository;
import com.akash.oyoclone.repository.HotelRepository;
import com.akash.oyoclone.repository.ReviewRepository;
import com.akash.oyoclone.service.ReviewService;
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
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {

        User currentUser = securityUtils.getCurrentUser();

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel", "id", request.getHotelId()));

        // ✅ ONLY VALID CHECK (Correct Logic)
        boolean hasConfirmedBookingForHotel =
                bookingRepository.findByUserId(currentUser.getId())
                        .stream()
                        .anyMatch(b ->
                                b.getRoom().getHotel().getId().equals(hotel.getId())
                                        && (b.getStatus() == BookingStatus.CONFIRMED
                                        || b.getStatus() == BookingStatus.COMPLETED)
                        );

        if (!hasConfirmedBookingForHotel) {
            throw new BadRequestException(
                    "You can only review hotels you have stayed at");
        }

        if (reviewRepository.existsByHotelIdAndUserId(
                hotel.getId(), currentUser.getId())) {

            throw new BadRequestException(
                    "You have already reviewed this hotel");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .hotel(hotel)
                .user(currentUser)
                .build();

        Review saved = reviewRepository.save(review);

        log.info("Review added for hotel {} by user {}",
                hotel.getId(), currentUser.getEmail());

        return mapToReviewResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByHotel(Long hotelId) {

        return reviewRepository.findByHotelId(hotelId)
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews() {

        User currentUser = securityUtils.getCurrentUser();

        return reviewRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .hotelId(review.getHotel().getId())
                .hotelName(review.getHotel().getName())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .createdAt(review.getCreatedAt())
                .build();
    }
}