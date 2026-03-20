package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.dto.BookingRequest;
import com.akash.oyoclone.dto.BookingResponse;
import com.akash.oyoclone.entity.*;
import com.akash.oyoclone.exception.BadRequestException;
import com.akash.oyoclone.exception.ResourceNotFoundException;
import com.akash.oyoclone.repository.BookingRepository;
import com.akash.oyoclone.repository.RoomRepository;
import com.akash.oyoclone.service.BookingService;
import com.akash.oyoclone.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        if (room.getAvailableRooms() < request.getNumberOfRooms()) {
            throw new BadRequestException(
                    "Not enough rooms available. Requested: " + request.getNumberOfRooms() +
                    ", Available: " + room.getAvailableRooms()
            );
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(request.getNumberOfRooms()));

        room.setAvailableRooms(room.getAvailableRooms() - request.getNumberOfRooms());
        roomRepository.save(room);

        Booking booking = Booking.builder()
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfRooms(request.getNumberOfRooms())
                .numberOfNights((int) nights)
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING_PAYMENT)
                .user(currentUser)
                .room(room)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: {} for user {}", saved.getId(), currentUser.getEmail());

        return mapToBookingResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings() {
        User currentUser = securityUtils.getCurrentUser();
        return bookingRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUpcomingBookings() {
        User currentUser = securityUtils.getCurrentUser();
        return bookingRepository.findUpcomingBookingsByUser(currentUser.getId(), LocalDate.now())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getPastBookings() {
        User currentUser = securityUtils.getCurrentUser();
        return bookingRepository.findPastBookingsByUser(currentUser.getId(), LocalDate.now())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }

        Room room = booking.getRoom();
        room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
        roomRepository.save(room);

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        log.info("Booking cancelled: {} by user {}", id, currentUser.getEmail());
        return mapToBookingResponse(updated);
    }

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        Room room = booking.getRoom();
        Hotel hotel = room.getHotel();

        return BookingResponse.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfRooms(booking.getNumberOfRooms())
                .numberOfNights(booking.getNumberOfNights())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())
                .roomId(room.getId())
                .roomType(room.getRoomType().name())
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .hotelCity(hotel.getCity())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
