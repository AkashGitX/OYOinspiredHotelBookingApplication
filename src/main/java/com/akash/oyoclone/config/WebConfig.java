package com.akash.oyoclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Path HOTEL_UPLOADS_DIR = Paths.get("uploads", "hotels");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure uploads dir exists, so images are immediately accessible after upload.
        try {
            Files.createDirectories(HOTEL_UPLOADS_DIR);
        } catch (Exception ignored) {
        }

        // Public mapping:
        // http://localhost:8080/hotel-images/{filename}
        registry.addResourceHandler("/hotel-images/**")
                .addResourceLocations("file:uploads/hotels/");
    }
}

