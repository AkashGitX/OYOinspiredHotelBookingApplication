package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.ReviewRequest;
import com.akash.oyoclone.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByHotel(Long hotelId);

    List<ReviewResponse> getUserReviews();
}
