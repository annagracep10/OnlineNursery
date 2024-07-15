package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.dto.CartDTO;
import com.techphantomexample.usermicroservice.dto.CartItemDTO;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMessageProducer cartMessageProducer;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;


    @Test
    void testAddItemToCart_NewItem() {
        int userId = 1;
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        CartItem cartItem = new CartItem();
        cartItem.setProductName("Product1");
        cartItem.setQuantity(1);

        when(userService.getCartByUserId(userId)).thenReturn(cart);
        cartService.addItemToCart(userId, cartItem);

        assertTrue(cart.getItems().contains(cartItem));
        assertEquals(1, cartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    void testAddItemToCart_ExistingItem() {
        int userId = 1;
        Cart cart = new Cart();
        CartItem existingItem = new CartItem();
        existingItem.setProductName("Product1");
        existingItem.setQuantity(1);
        cart.setItems(new ArrayList<>(List.of(existingItem)));
        CartItem cartItem = new CartItem();
        cartItem.setProductName("Product1");
        cartItem.setQuantity(2);
        when(userService.getCartByUserId(userId)).thenReturn(cart);

        cartService.addItemToCart(userId, cartItem);

        assertEquals(3, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void testRemoveItemFromCart_ItemExists() {
        int userId = 1;
        int itemId = 2;
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        when(userService.getCartByUserId(userId)).thenReturn(cart);

        cartService.removeItemFromCart(userId, itemId);

        assertFalse(cart.getItems().contains(cartItem));
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void testRemoveItemFromCart_ItemNotFound() {
        int userId = 1;
        int itemId = 2;
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(userService.getCartByUserId(userId)).thenReturn(cart);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cartService.removeItemFromCart(userId, itemId);
        });

        assertEquals("Item not found in cart with id: " + itemId, exception.getMessage());
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void testCheckout() throws JsonProcessingException {
        int userId = 1;
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        when(userService.getCartByUserId(userId)).thenReturn(cart);
        CartItemDTO cartItemDto = new CartItemDTO();
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDto);

        cartService.checkout(userId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository, times(1)).deleteAll(cart.getItems());
        verify(cartRepository, times(1)).save(cart);
        verify(cartMessageProducer, times(1)).sendCartItemsAsJson(any(CartDTO.class));
    }
}
