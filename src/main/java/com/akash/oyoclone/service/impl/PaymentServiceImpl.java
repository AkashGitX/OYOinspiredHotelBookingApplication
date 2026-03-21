package com.akash.oyoclone.service.impl;

import com.akash.oyoclone.dto.PaymentResponse;
import com.akash.oyoclone.entity.Booking;
import com.akash.oyoclone.entity.BookingStatus;
import com.akash.oyoclone.entity.Payment;
import com.akash.oyoclone.entity.PaymentStatus;
import com.akash.oyoclone.exception.PaymentException;
import com.akash.oyoclone.exception.ResourceNotFoundException;
import com.akash.oyoclone.repository.BookingRepository;
import com.akash.oyoclone.repository.PaymentRepository;
import com.akash.oyoclone.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    @Transactional
    public PaymentResponse createCheckoutSession(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking", "id", bookingId));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new PaymentException("Booking already paid");
        }

        try {

            long amountInCents = booking.getTotalPrice()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValue();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)

                            // ✅ FIXED SUCCESS REDIRECT (VERY IMPORTANT)
                            .setSuccessUrl(
                                    "http://oyoinspiredhotelbookingapplication-production.up.railway.app/payment/success" +
                                            "?session_id={CHECKOUT_SESSION_ID}"
                            )

                            // ✅ FIXED CANCEL REDIRECT
                            .setCancelUrl(
                                    "http://oyoinspiredhotelbookingapplication-production.up.railway.app/payment/cancel" +
                                            "?booking_id=" + bookingId
                            )

                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr")
                                                            .setUnitAmount(amountInCents)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Hotel Booking - " +
                                                                                    booking.getRoom()
                                                                                            .getHotel()
                                                                                            .getName())
                                                                            .setDescription(
                                                                                    booking.getRoom()
                                                                                            .getRoomType()
                                                                                            .name()
                                                                                            + " | "
                                                                                            + booking.getNumberOfNights()
                                                                                            + " nights | "
                                                                                            + booking.getNumberOfRooms()
                                                                                            + " room(s)"
                                                                            )
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )

                            // metadata useful later
                            .putMetadata("bookingId", bookingId.toString())

                            .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .stripeSessionId(session.getId())
                    .amount(booking.getTotalPrice())
                    .status(PaymentStatus.PENDING)
                    .currency("inr")
                    .booking(booking)
                    .build();

            paymentRepository.save(payment);

            log.info("Stripe session created for booking {} : {}",
                    bookingId, session.getId());

            return PaymentResponse.builder()
                    .stripeSessionId(session.getId())
                    .amount(booking.getTotalPrice())
                    .status(PaymentStatus.PENDING)
                    .currency("inr")
                    .bookingId(bookingId)
                    .checkoutUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe checkout error: {}", e.getMessage());
            throw new PaymentException("Stripe checkout failed", e);
        }
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(String sessionId) {

        Payment payment = paymentRepository
                .findByStripeSessionId(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment", "sessionId", sessionId));

        try {

            Session session = Session.retrieve(sessionId);
            String paymentIntentId = session.getPaymentIntent();

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setStripeTransactionId(paymentIntentId);
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            log.info("Payment confirmed booking {} txn {}",
                    booking.getId(), paymentIntentId);

        } catch (StripeException e) {
            log.error("Stripe confirm error: {}", e.getMessage());
            throw new PaymentException("Payment confirmation failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId) {

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment", "bookingId", bookingId));

        return PaymentResponse.builder()
                .id(payment.getId())
                .stripeSessionId(payment.getStripeSessionId())
                .stripeTransactionId(payment.getStripeTransactionId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .currency(payment.getCurrency())
                .bookingId(bookingId)
                .createdAt(payment.getCreatedAt())
                .build();
    }
}