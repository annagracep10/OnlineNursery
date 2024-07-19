package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.dto.CartItemDTO;
import com.techphantomexample.usermicroservice.dto.PlantDTO;
import com.techphantomexample.usermicroservice.dto.PlanterDTO;
import com.techphantomexample.usermicroservice.dto.SeedDTO;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.model.CartResponse;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    public String productServiceBaseUrl;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable int userId) {
        Cart cart = userService.getCartByUserId(userId);
        CartResponse cartResponse = new CartResponse("Cart displayed",HttpStatus.OK.value(),cart);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);

    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable int userId, @RequestBody CartItemDTO cartItemDto) {
        CartResponse cartResponse = cartService.addItemToCart(userId, cartItemDto);
        return new ResponseEntity<>(cartResponse, HttpStatus.valueOf(cartResponse.getStatus()));
    }


}
