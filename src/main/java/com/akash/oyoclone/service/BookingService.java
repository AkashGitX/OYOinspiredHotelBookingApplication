package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.BookingRequest;
import com.akash.oyoclone.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getUserBookings();

    List<BookingResponse> getUpcomingBookings();

    List<BookingResponse> getPastBookings();

    BookingResponse cancelBooking(Long id);
}
