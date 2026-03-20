package com.akash.oyoclone.controller;

import com.akash.oyoclone.dto.ApiResponse;
import com.akash.oyoclone.dto.PaymentResponse;
import com.akash.oyoclone.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> createCheckoutSession(
            @PathVariable Long bookingId) {
        PaymentResponse response = paymentService.createCheckoutSession(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Checkout session created", response));
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(
            @RequestParam("session_id") String sessionId) {
        paymentService.handlePaymentSuccess(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed successfully"));
    }

    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> paymentCancelled(
            @RequestParam("booking_id") Long bookingId) {
        log.info("Payment cancelled for booking {}", bookingId);
        return ResponseEntity.ok(ApiResponse.success("Payment was cancelled"));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByBooking(
            @PathVariable Long bookingId) {
        PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Payment fetched successfully", payment));
    }
}
