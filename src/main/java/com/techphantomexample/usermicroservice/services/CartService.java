package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.model.Cart;
import com.techphantomexample.usermicroservice.model.CartItem;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartService(UserRepository userRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getCartByUserId(int userId) {
        return cartRepository.findByUser_UserId(userId);
    }

    public void addItemToCart(String userEmail, CartItem cartItem) {
        User user = userRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Cart cart = cartRepository.findByUser_UserId(user.getUserId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }

        cartItem.setCart(cart);
        cart.getItems().add(cartItem);
        cartItemRepository.save(cartItem);
    }

    public void removeItemFromCart(int userId, int itemId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        if (cart != null) {
            List<CartItem> items = cart.getItems();
            CartItem itemToRemove = items.stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
            if (itemToRemove != null) {
                items.remove(itemToRemove);
                cartItemRepository.delete(itemToRemove);
            }
        }
    }

    public void checkout(int userId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }
}
