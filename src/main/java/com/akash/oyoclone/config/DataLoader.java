package com.akash.oyoclone.config;

import com.akash.oyoclone.entity.*;
import com.akash.oyoclone.repository.HotelRepository;
import com.akash.oyoclone.repository.RoomRepository;
import com.akash.oyoclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Sample data already exists, skipping data loader.");
            return;
        }

        log.info("Loading sample data...");

        User owner = User.builder()
                .fullName("Rajesh Kumar")
                .email("owner@oyo.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.HOTEL_OWNER)
                .enabled(true)
                .build();
        userRepository.save(owner);

        User user = User.builder()
                .fullName("Amit Sharma")
                .email("user@oyo.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        Hotel hotel1 = Hotel.builder()
                .name("OYO 1234 Grand Palace")
                .city("Mumbai")
                .description("A luxurious hotel in the heart of Mumbai with world-class amenities and stunning city views.")
                .amenities("WiFi, AC, Parking, Swimming Pool, Gym, Restaurant, Bar, Room Service, Spa")
                .imagePath(null)
                .approved(true)
                .owner(owner)
                .build();
        hotelRepository.save(hotel1);

        addRooms(hotel1, RoomType.STANDARD, new BigDecimal("1499"), 10, 2);
        addRooms(hotel1, RoomType.DELUXE, new BigDecimal("2499"), 5, 2);
        addRooms(hotel1, RoomType.SUITE, new BigDecimal("4999"), 3, 4);

        Hotel hotel2 = Hotel.builder()
                .name("OYO Flagship Business Inn")
                .city("Delhi")
                .description("Modern business hotel in Delhi offering premium services for corporate travelers.")
                .amenities("WiFi, AC, Conference Room, Restaurant, Laundry, 24/7 Reception")
                .imagePath(null)
                .approved(true)
                .owner(owner)
                .build();
        hotelRepository.save(hotel2);

        addRooms(hotel2, RoomType.STANDARD, new BigDecimal("1299"), 15, 2);
        addRooms(hotel2, RoomType.DELUXE, new BigDecimal("1999"), 8, 2);

        Hotel hotel3 = Hotel.builder()
                .name("OYO Townhouse Beach Resort")
                .city("Goa")
                .description("Beachside resort with stunning ocean views and tropical surroundings. Perfect for a relaxing vacation.")
                .amenities("WiFi, AC, Beach Access, Swimming Pool, Restaurant, Water Sports, Bar")
                .imagePath(null)
                .approved(true)
                .owner(owner)
                .build();
        hotelRepository.save(hotel3);

        addRooms(hotel3, RoomType.STANDARD, new BigDecimal("1799"), 12, 2);
        addRooms(hotel3, RoomType.DELUXE, new BigDecimal("2999"), 6, 2);
        addRooms(hotel3, RoomType.SUITE, new BigDecimal("5999"), 4, 4);

        log.info("Sample data loaded successfully!");
        log.info("Test credentials:");
        log.info("  Hotel Owner -> email: owner@oyo.com | password: password123");
        log.info("  User        -> email: user@oyo.com  | password: password123");
    }

    private void addRooms(Hotel hotel, RoomType type, BigDecimal price, int total, int capacity) {
        Room room = Room.builder()
                .roomType(type)
                .pricePerNight(price)
                .totalRooms(total)
                .availableRooms(total)
                .capacity(capacity)
                .hotel(hotel)
                .build();
        roomRepository.save(room);
    }
}
