package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.exception.BadRequestException;
import com.akash.oyoclone.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final long MAX_BYTES = 5L * 1024L * 1024L; // 5MB
    private static final Path HOTEL_UPLOADS_DIR =
            Paths.get("uploads", "hotels");

    @Override
    public String storeHotelImage(Long hotelId, MultipartFile file) {
        if (hotelId == null || hotelId <= 0) {
            throw new BadRequestException("Invalid hotel id");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Hotel image is required");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BadRequestException("Hotel image must be <= 5MB");
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";

        boolean allowed =
                contentType.equals("image/jpeg") || contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png");

        if (!allowed) {
            throw new BadRequestException("Only JPG/PNG images are allowed");
        }

        BufferedImage image;
        try (InputStream is = file.getInputStream()) {
            image = ImageIO.read(is);
        } catch (IOException e) {
            log.error("Failed reading uploaded hotel image", e);
            throw new BadRequestException("Invalid image file");
        }

        if (image == null) {
            throw new BadRequestException("Invalid image file");
        }

        try {
            Files.createDirectories(HOTEL_UPLOADS_DIR);
        } catch (IOException e) {
            log.error("Could not create hotel uploads directory", e);
            throw new BadRequestException("Server error while saving image");
        }

        String filename = "hotel_" + hotelId + "_" + System.currentTimeMillis() + ".jpg";
        Path destination = HOTEL_UPLOADS_DIR.resolve(filename);

        try {
            // Always store as JPG to keep filename format stable (hotel_<id>_<timestamp>.jpg).
            ImageIO.write(image, "jpg", destination.toFile());
        } catch (IOException e) {
            log.error("Failed saving hotel image {}", destination, e);
            throw new BadRequestException("Server error while saving image");
        }

        return filename;
    }
}

