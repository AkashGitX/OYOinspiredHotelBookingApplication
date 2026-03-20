package com.akash.oyoclone.dto;

import com.akash.oyoclone.entity.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Price per night is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "Total rooms must be at least 1")
    private int totalRooms;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
}
