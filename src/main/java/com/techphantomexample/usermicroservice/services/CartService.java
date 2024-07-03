package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import com.techphantomexample.usermicroservice.Dto.CartItemDTO;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
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


    public void addItemToCart(int userId, CartItem cartItem) {
        Cart cart = userService.getCartByUserId(userId);
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
