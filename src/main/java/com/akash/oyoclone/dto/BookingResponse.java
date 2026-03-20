package com.akash.oyoclone.dto;

import com.akash.oyoclone.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfRooms;
    private int numberOfNights;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private Long userId;
    private String userName;
    private Long roomId;
    private String roomType;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private LocalDateTime createdAt;
}
