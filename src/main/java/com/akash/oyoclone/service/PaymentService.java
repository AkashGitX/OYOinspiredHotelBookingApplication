package com.akash.oyoclone.service;

import com.akash.oyoclone.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createCheckoutSession(Long bookingId);

    void handlePaymentSuccess(String sessionId);

    PaymentResponse getPaymentByBookingId(Long bookingId);
}
