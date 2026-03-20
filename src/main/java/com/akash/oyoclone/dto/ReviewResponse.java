package com.akash.oyoclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private int rating;
    private String comment;
    private Long hotelId;
    private String hotelName;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}
