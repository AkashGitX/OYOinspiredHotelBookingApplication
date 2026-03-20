package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.dto.HotelRequest;
import com.akash.oyoclone.dto.HotelResponse;
import com.akash.oyoclone.dto.RoomResponse;
import com.akash.oyoclone.entity.Hotel;
import com.akash.oyoclone.entity.User;
import com.akash.oyoclone.exception.ResourceNotFoundException;
import com.akash.oyoclone.exception.UnauthorizedException;
import com.akash.oyoclone.repository.HotelRepository;
import com.akash.oyoclone.repository.ReviewRepository;
import com.akash.oyoclone.service.HotelService;
import com.akash.oyoclone.service.ImageStorageService;
import com.akash.oyoclone.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final SecurityUtils securityUtils;
    private final ImageStorageService imageStorageService;

    @Override
    @Transactional
    public HotelResponse createHotel(HotelRequest request, MultipartFile image) {
        User currentUser = securityUtils.getCurrentUser();

        // Persist first so we have a generated hotel id for the image filename.
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .city(request.getCity())
                .description(request.getDescription())
                .amenities(request.getAmenities())
                .approved(true)
                .owner(currentUser)
                .build();

        Hotel saved = hotelRepository.save(hotel);

        String imageFilename = imageStorageService.storeHotelImage(saved.getId(), image);
        saved.setImagePath(imageFilename);
        saved = hotelRepository.save(saved);

        log.info("Hotel created: {} by owner {}", saved.getName(), currentUser.getEmail());

        return mapToHotelResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        return mapToHotelResponse(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponse> getAllApprovedHotels() {
        return hotelRepository.findByApprovedTrue()
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponse> searchHotels(String query) {
        return hotelRepository.searchHotels(query)
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponse> searchByCity(String city) {
        return hotelRepository.findByCityContainingIgnoreCaseAndApprovedTrue(city)
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponse> getOwnerHotels() {
        User currentUser = securityUtils.getCurrentUser();
        return hotelRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));

        User currentUser = securityUtils.getCurrentUser();
        if (!hotel.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own hotels");
        }

        hotel.setName(request.getName());
        hotel.setCity(request.getCity());
        hotel.setDescription(request.getDescription());
        hotel.setAmenities(request.getAmenities());

        Hotel updated = hotelRepository.save(hotel);
        return mapToHotelResponse(updated);
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));

        User currentUser = securityUtils.getCurrentUser();
        if (!hotel.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own hotels");
        }

        hotelRepository.delete(hotel);
        log.info("Hotel deleted: {} by owner {}", id, currentUser.getEmail());
    }

    private HotelResponse mapToHotelResponse(Hotel hotel) {
        Double avgRating = reviewRepository.findAverageRatingByHotelId(hotel.getId());
        long totalReviews = reviewRepository.countByHotelId(hotel.getId());

        String imageUrl = null;
        String imagePath = null;
        if (hotel.getImagePath() != null && !hotel.getImagePath().isBlank()) {
            String filenameOnly = java.nio.file.Paths.get(hotel.getImagePath()).getFileName().toString();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
            imagePath = baseUrl + "/hotel-images/" + filenameOnly;
            imageUrl = imagePath; // keep backward compatibility
        }

        List<RoomResponse> rooms = hotel.getRooms().stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .roomType(room.getRoomType())
                        .pricePerNight(room.getPricePerNight())
                        .totalRooms(room.getTotalRooms())
                        .availableRooms(room.getAvailableRooms())
                        .capacity(room.getCapacity())
                        .hotelId(hotel.getId())
                        .hotelName(hotel.getName())
                        .build())
                .collect(Collectors.toList());

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .description(hotel.getDescription())
                .amenities(hotel.getAmenities())
                .imagePath(imagePath)
                .imageUrl(imageUrl)
                .approved(hotel.isApproved())
                .ownerId(hotel.getOwner().getId())
                .ownerName(hotel.getOwner().getFullName())
                .rooms(rooms)
                .averageRating(avgRating)
                .totalReviews((int) totalReviews)
                .createdAt(hotel.getCreatedAt())
                .build();
    }
}
