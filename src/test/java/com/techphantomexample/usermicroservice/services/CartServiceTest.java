package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.repository.CartItemRepository;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@MockitoSettings
class CartServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMessageProducer cartMessageProducer;

    @Mock
    ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private CartItem cartItem1;
    private CartItem cartItem2;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUserEmail("test@example.com");
        user.setUserId(1);

        cartItem1 = new CartItem();
        cartItem1.setId(1);
        cartItem1.setProductName("Test Product 1");
        cartItem1.setQuantity(1);

        cartItem2 = new CartItem();
        cartItem2.setId(2);
        cartItem2.setProductName("Test Product 2");
        cartItem2.setQuantity(2);

        cart = new Cart();
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem1);
        items.add(cartItem2);
        cart.setItems(items);
        cart.setUser(user);

    }

    @Test
    public void testAddItemToCart_NewCart_NewItem() {
        when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
        when(userService.getCartByUserId(user.getUserId())).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        CartItem newCartItem = new CartItem();
        newCartItem.setProductName("Test Product 3");
        newCartItem.setQuantity(1);

        cartService.addItemToCart(user.getUserEmail(), newCartItem);

        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    public void testAddItemToCart_ExistingCart_NewItem() {
        when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
        when(userService.getCartByUserId(user.getUserId())).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        CartItem newCartItem = new CartItem();
        newCartItem.setProductName("Test Product 3");
        newCartItem.setQuantity(1);

        cartService.addItemToCart(user.getUserEmail(), newCartItem);

        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    public void testAddItemToCart_ExistingCart_ExistingItem() {
        when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
        when(userService.getCartByUserId(user.getUserId())).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        CartItem newCartItem = new CartItem();
        newCartItem.setProductName("Test Product 1");
        newCartItem.setQuantity(2);

        cartService.addItemToCart(user.getUserEmail(), newCartItem);

        assertEquals(3, cartItem1.getQuantity());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository).save(cartItem1);
    }


    @Test
    public void testRemoveItemFromCart_ItemFound() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(cart);

        cartService.removeItemFromCart(1, 1);

        assertFalse(cart.getItems().contains(cartItem1));
        verify(cartItemRepository).delete(cartItem1);
    }

    @Test
    public void testRemoveItemFromCart_ItemNotFound() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(cart);
        cartService.removeItemFromCart(1, 3);
        assertTrue(cart.getItems().contains(cartItem1));
        assertTrue(cart.getItems().contains(cartItem2));
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    public void testRemoveItemFromCart_CartNotFound() {
        when(cartRepository.findByUser_UserId(1)).thenReturn(null);
        cartService.removeItemFromCart(1, 1);
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    public void testCheckout_CartExists() throws JsonProcessingException {
        when(cartRepository.findByUser_UserId(user.getUserId())).thenReturn(cart);
        doNothing().when(cartItemRepository).deleteAll(cart.getItems());
        cartService.checkout(user.getUserId());
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
        verify(cartMessageProducer, times(1)).sendCartItemsAsJson(any(CartDTO.class));
    }

    @Test
    public void testCheckout_CartDoesNotExist() throws JsonProcessingException {
        when(cartRepository.findByUser_UserId(user.getUserId())).thenReturn(null);
        cartService.checkout(user.getUserId());
        verify(cartRepository, never()).save(any());
        verify(cartMessageProducer, never()).sendCartItemsAsJson(any());
    }
}