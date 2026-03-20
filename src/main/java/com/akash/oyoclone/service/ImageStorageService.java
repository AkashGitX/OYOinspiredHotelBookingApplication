package com.akash.oyoclone.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    /**
     * Stores the uploaded hotel image in {@code uploads/hotels/}.
     *
     * @return stored filename (e.g. hotel_1_1700000000000.jpg)
     */
    String storeHotelImage(Long hotelId, MultipartFile file);
}

