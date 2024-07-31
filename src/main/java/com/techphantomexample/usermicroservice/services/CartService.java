package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.dto.*;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.Order;
import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.messege.SendOrderMessage;
import com.techphantomexample.usermicroservice.model.CartResponse;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SendOrderMessage sendOrderMessage;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private  UserService userService;

    @Autowired
    private  OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    @Autowired
    ProductUpdateService productUpdateService;

    public CartResponse addItemToCart(int userId, CartItemDTO cartItemDto) {
        Cart cart = userService.getCartByUserId(userId);
        CartItem cartItem = new CartItem();
        cartItem.setProductName(cartItemDto.getProductName());
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItem.setProductType(cartItemDto.getProductType());
        String url = productServiceBaseUrl;
        int quantity;
        double price;
        int id;

        switch (cartItemDto.getProductType()) {
            case "plant":
                url += "/plant/" + cartItemDto.getProductId();
                PlantDTO plant = restTemplate.getForObject(url, PlantDTO.class);
                quantity = plant.getQuantity();
                price = plant.getPrice();
                id = plant.getId();
                break;
            case "planter":
                url += "/planter/" + cartItemDto.getProductId();
                PlanterDTO planter = restTemplate.getForObject(url, PlanterDTO.class);
                quantity = planter.getQuantity();
                price = planter.getPrice();
                id = planter.getId();
                break;
            case "seed":
                url += "/seed/" + cartItemDto.getProductId();
                SeedDTO seed = restTemplate.getForObject(url, SeedDTO.class);
                quantity = seed.getQuantity();
                price = seed.getPrice();
                id= seed.getId();
                break;
            default:
                return new CartResponse("Product Type Unavailable", HttpStatus.BAD_REQUEST.value(), cart);
        }

        if (cartItemDto.getQuantity() > quantity) {
            return new CartResponse("Product out of Stock - Only " + quantity + " left", HttpStatus.BAD_REQUEST.value(), cart);
        }

        cartItem.setProductId(id);
        cartItem.setPrice(price);

        Optional<CartItem> existingItemOptional = cart.getItems().stream()
                .filter(item -> item.getProductName().equals(cartItem.getProductName()))
                .findFirst();

        if (existingItemOptional.isPresent()) {
            CartItem existingItem = existingItemOptional.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }
        productUpdateService.updateProductQuantity(cartItemDto.getProductId(), cartItemDto.getProductType(), -cartItemDto.getQuantity());
        return new CartResponse("Product added successfully", HttpStatus.OK.value(), cart);
    }

    public void removeItemFromCart(int userId, int itemId) {
        Cart cart = userService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> items = cart.getItems();
            CartItem itemToRemove = items.stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
            if (itemToRemove != null) {
                items.remove(itemToRemove);
                productUpdateService.updateProductQuantity(itemToRemove.getProductId(), itemToRemove.getProductType(), itemToRemove.getQuantity());
                cartItemRepository.delete(itemToRemove);
            }else {
                throw new NotFoundException("Item not found in cart with id: " + itemId);
            }
        }
    }



    public CartResponse checkout(int userId) throws JsonProcessingException {
        Cart cart = userService.getCartByUserId(userId);
        if (cart != null) {
            Order order = new Order();
            order.setUserId(userId);
            if(!cart.getItems().isEmpty()){
                List<OrderItem> orderItems = cart.getItems().stream()
                        .map(cartItem -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setProductId(cartItem.getProductId());
                            orderItem.setProductName(cartItem.getProductName());
                            orderItem.setQuantity(cartItem.getQuantity());
                            orderItem.setPrice(cartItem.getPrice());
                            orderItem.setProductType(cartItem.getProductType());
                            return orderItem;
                        })
                        .collect(Collectors.toList());
                order.setItems(orderItems);
                orderService.saveOrder(order);
                sendOrderMessage.sendOrderAsJson(order);
                cartItemRepository.deleteAll(cart.getItems());
                cart.getItems().clear();
                cartRepository.save(cart);
                return new CartResponse("Checkout Success",HttpStatus.OK.value(), order);
            }
            else
                return new CartResponse("No item in your cart ",HttpStatus.BAD_REQUEST.value(), cart);

        }
        return new CartResponse("No such cart found", HttpStatus.BAD_REQUEST.value(), null);
    }

}
