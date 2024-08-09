package com.techphantomexample.usermicroservice.api_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.dto.*;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.entity.Orders;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.messege.SendOrderMessage;
import com.techphantomexample.usermicroservice.model.CartResponse;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.OrderService;
import com.techphantomexample.usermicroservice.services.RazorPayOrderService;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @Autowired
    OrderService orderService;

    @Autowired
    private RazorPayOrderService razorPayOrderService;

    @Autowired
    private AuthController authController;

    @Autowired
    private SendOrderMessage sendOrderMessage;

    @Value("${product.service.base-url}")
    public String productServiceBaseUrl;


    @GetMapping
    public ResponseEntity<CartResponse> getCartByUserId() {
        int userId = authController.getCurrentUserId();
        Cart cart = userService.getCartByUserId(userId);
        CartResponse cartResponse = new CartResponse("Cart displayed", HttpStatus.OK.value(), cart);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(@RequestBody CartItemDTO cartItemDto) {
        int userId = authController.getCurrentUserId();
        CartResponse cartResponse = cartService.addItemToCart(userId, cartItemDto);
        return new ResponseEntity<>(cartResponse, HttpStatus.valueOf(cartResponse.getStatus()));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable int itemId) {
        int userId = authController.getCurrentUserId();
        try {
            cartService.removeItemFromCart(userId, itemId);
            Cart cart = userService.getCartByUserId(userId);
            CartResponse cartResponse = new CartResponse("Item removed successfully", HttpStatus.OK.value(), cart);
            return new ResponseEntity<>(cartResponse, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new CartResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<CartResponse> checkout() {
        int userId = authController.getCurrentUserId();
        try {
            CartResponse cartResponse = cartService.checkout(userId);
            return new ResponseEntity<>(cartResponse, HttpStatus.valueOf(cartResponse.getStatus()));
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(new CartResponse("Checkout failed", HttpStatus.INTERNAL_SERVER_ERROR.value(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/verifyPayment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) throws JsonProcessingException {
        boolean isPaymentValid = razorPayOrderService.verifyPayment(request);
        if (isPaymentValid) {
            Orders order = orderService.findByRazorpayOrderId(request.getRazorpayOrderId());
            order.setStatus(OrderStatus.ORDER_RECEIVED);
            orderService.saveOrder(order);

            cartService.clearCart(order.getUserId());

            sendOrderMessage.sendOrderAsJson(order);

            return ResponseEntity.ok(new CartResponse("Payment verified and order placed successfully", HttpStatus.OK.value(), order));
        } else {
            Orders order = orderService.findByRazorpayOrderId(request.getRazorpayOrderId());
            Cart cart = userService.getCartByUserId(order.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CartResponse("Payment verification failed", HttpStatus.BAD_REQUEST.value(), cart));
        }
    }
}
