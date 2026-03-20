package com.akash.oyoclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {

    private Long id;
    private String name;
    private String city;
    private String description;
    private String amenities;
    // Full URL to the hosted image, e.g. http://localhost:8080/hotel-images/hotel_7_....
    private String imagePath;

    // Backward compatibility (older frontend uses imageUrl).
    private String imageUrl;
    private boolean approved;
    private Long ownerId;
    private String ownerName;
    private List<RoomResponse> rooms;
    private Double averageRating;
    private int totalReviews;
    private LocalDateTime createdAt;
}
