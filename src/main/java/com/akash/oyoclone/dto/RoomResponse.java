package com.akash.oyoclone.dto;

import com.akash.oyoclone.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private int totalRooms;
    private int availableRooms;
    private int capacity;
    private Long hotelId;
    private String hotelName;
}
