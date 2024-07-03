package com.techphantomexample.usermicroservice.webController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartWebControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private CartWebController cartWebController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testViewCart() {
        User user = new User();
        user.setUserId(1);
        Cart cart = new Cart();
        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getCartByUserId(1)).thenReturn(cart);

        String viewName = cartWebController.viewCart(model, session);

        assertEquals("cart", viewName);
        verify(model, times(1)).addAttribute("cart", cart);
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testAddToCart() {
        User user = new User();
        user.setUserEmail("test@example.com");
        CartItem cartItem = new CartItem();
        when(session.getAttribute("user")).thenReturn(user);

        String viewName = cartWebController.addToCart(cartItem, session);

        assertEquals("redirect:/user/products", viewName);
    }

    @Test
    public void testRemoveFromCart() {
        User user = new User();
        user.setUserId(1);
        when(session.getAttribute("user")).thenReturn(user);

        String viewName = cartWebController.removeFromCart(1, session);

        assertEquals("redirect:/user/cart", viewName);
        verify(cartService, times(1)).removeItemFromCart(1, 1);
    }

    @Test
    public void testCheckout() throws JsonProcessingException {
        User user = new User();
        user.setUserId(1);
        when(session.getAttribute("user")).thenReturn(user);

        String viewName = cartWebController.checkout(session);

        assertEquals("redirect:/user/cart", viewName);
        verify(cartService, times(1)).checkout(1);
    }
}
