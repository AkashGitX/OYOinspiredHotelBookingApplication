package com.akash.oyoclone.dto;

import com.akash.oyoclone.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String stripeSessionId;
    private String stripeTransactionId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String currency;
    private Long bookingId;
    private String checkoutUrl;
    private LocalDateTime createdAt;
}
