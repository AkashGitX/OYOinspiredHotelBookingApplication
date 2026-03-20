package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.HotelRequest;
import com.akash.oyoclone.dto.HotelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HotelService {

    HotelResponse createHotel(HotelRequest request, MultipartFile image);

    HotelResponse getHotelById(Long id);

    List<HotelResponse> getAllApprovedHotels();

    List<HotelResponse> searchHotels(String query);

    List<HotelResponse> searchByCity(String city);

    List<HotelResponse> getOwnerHotels();

    HotelResponse updateHotel(Long id, HotelRequest request);

    void deleteHotel(Long id);
}
