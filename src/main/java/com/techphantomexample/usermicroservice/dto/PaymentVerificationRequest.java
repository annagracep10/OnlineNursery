package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationRequest {
    private String razorpayOrderId;
    private String paymentId;
    private String signature;
}
