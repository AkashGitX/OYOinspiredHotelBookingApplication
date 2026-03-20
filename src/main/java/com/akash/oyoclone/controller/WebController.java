package com.akash.oyoclone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/hotels/{id}")
    public String hotelDetails(@PathVariable Long id) {
        return "hotel-details";
    }

    @GetMapping("/booking/checkout")
    public String bookingCheckout() {
        return "booking-checkout";
    }

    @GetMapping("/payment/redirect")
    public String paymentRedirect() {
        return "payment-redirect";
    }

    @GetMapping("/payment/success")
    public String paymentSuccess() {
        return "payment-success";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile-dashboard";
    }

    @GetMapping("/owner/dashboard")
    public String ownerDashboard() {
        return "owner-dashboard";
    }

    @GetMapping("/owner/hotels/add")
    public String addHotel() {
        return "add-hotel";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "payment-cancel";
    }

    @GetMapping("/owner/hotels/{hotelId}/rooms/add")
    public String addRoom(@PathVariable Long hotelId) {
        return "add-room";
    }
}
