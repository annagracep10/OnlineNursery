package com.techphantomexample.usermicroservice.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.techphantomexample.usermicroservice.dto.PaymentVerificationRequest;
import com.techphantomexample.usermicroservice.dto.RazorpayOrderResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class RazorPayOrderService {

    @Value("${razorpay.key.id}")
    private  String RAZORPAY_KEY ;
    @Value("${razorpay.secret.key}")
    private  String RAZORPAY_SECRET ;

    public RazorpayOrderResponse createRazorpayOrder(double totalAmount, int orderId) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount",  (int)totalAmount * 100 );
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptId" + orderId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            RazorpayOrderResponse razorpayOrderResponse = new RazorpayOrderResponse();
            razorpayOrderResponse.setId(razorpayOrder.get("id"));
            razorpayOrderResponse.setCurrency(razorpayOrder.get("currency"));
            int amountInPaise = (int) razorpayOrder.get("amount");
            razorpayOrderResponse.setAmount(amountInPaise);

            return razorpayOrderResponse;
        } catch (RazorpayException e) {
            throw new RuntimeException("Razorpay order creation failed", e);
        }
    }

    public boolean verifyPayment(PaymentVerificationRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("razorpay_order_id", request.getRazorpayOrderId());
            params.put("razorpay_payment_id", request.getPaymentId());
            params.put("razorpay_signature", request.getSignature());

            JSONObject jsonParams = new JSONObject(params);
            return Utils.verifyPaymentSignature(jsonParams, RAZORPAY_SECRET);
        } catch (Exception e) {
            return false;
        }
    }


}
