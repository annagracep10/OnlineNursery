package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.dto.*;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.model.CartResponse;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartMessageProducer cartMessageProducer;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private  UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    public CartResponse addItemToCart(int userId, CartItemDTO cartItemDto) {
        Cart cart = userService.getCartByUserId(userId);
        CartItem cartItem = new CartItem();
        cartItem.setProductName(cartItemDto.getProductName());
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItem.setProductType(cartItemDto.getProductType());
        String url = productServiceBaseUrl;
        int quantity;
        double price;

        switch (cartItemDto.getProductType()) {
            case "plant":
                url += "/plant/" + cartItemDto.getProductId();
                PlantDTO plant = restTemplate.getForObject(url, PlantDTO.class);
                quantity = plant.getQuantity();
                price = plant.getPrice();
                break;
            case "planter":
                url += "/planter/" + cartItemDto.getProductId();
                PlanterDTO planter = restTemplate.getForObject(url, PlanterDTO.class);
                quantity = planter.getQuantity();
                price = planter.getPrice();
                break;
            case "seed":
                url += "/seed/" + cartItemDto.getProductId();
                SeedDTO seed = restTemplate.getForObject(url, SeedDTO.class);
                quantity = seed.getQuantity();
                price = seed.getPrice();
                break;
            default:
                return new CartResponse("Product Type Unavailable", HttpStatus.BAD_REQUEST.value(), cart);
        }

        if (cartItemDto.getQuantity() > quantity) {
            return new CartResponse("Product out of Stock - Only " + quantity + " left", HttpStatus.BAD_REQUEST.value(), cart);
        }

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
        updateProductQuantity(cartItemDto.getProductId(), cartItemDto.getProductType(), cartItemDto.getQuantity());
        return new CartResponse("Product added successfully", HttpStatus.OK.value(), cart);
    }

    private void updateProductQuantity(int productId, String productType, int quantityToSubtract) {
        String url = productServiceBaseUrl + "/" + productType + "/" + productId + "/quantity";
        Map<String, Integer> updateQuantityMap = new HashMap<>();
        updateQuantityMap.put("quantityToSubtract", quantityToSubtract);
        restTemplate.put(url, updateQuantityMap);
    }

    public void removeItemFromCart(int userId, int itemId) {
        Cart cart = userService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> items = cart.getItems();
            CartItem itemToRemove = items.stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
            if (itemToRemove != null) {
                items.remove(itemToRemove);
                cartItemRepository.delete(itemToRemove);
            }else {
                throw new NotFoundException("Item not found in cart with id: " + itemId);
            }
        }
    }

    public void checkout(int userId) throws JsonProcessingException {
        Cart cart = userService.getCartByUserId(userId);
        CartDTO cartDto = new CartDTO();
        if (cart != null) {
            List<CartItemDTO> itemDTOs = cart.getItems().stream()
                    .map(item -> modelMapper.map(item, CartItemDTO.class))
                    .collect(Collectors.toList());
            cartDto.setItems(itemDTOs);
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
            cartMessageProducer.sendCartItemsAsJson(cartDto);
        }
    }

}
