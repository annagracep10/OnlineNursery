package com.techphantomexample.usermicroservice.webController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartWebController.class)
@ExtendWith(MockitoExtension.class)
public class CartWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        session = new MockHttpSession();
        User user = new User();
        user.setUserId(1);
        session.setAttribute("user", user);
    }

    @Test
    public void testViewCart() throws Exception {
        Cart cart = new Cart();
        when(userService.getCartByUserId(1)).thenReturn(cart);

        mockMvc.perform(get("/user/cart")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("cart", cart))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testAddToCart() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setId(1);

        mockMvc.perform(post("/user/addToCart")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("cartItemId", String.valueOf(cartItem.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/products"));
    }

    @Test
    public void testRemoveFromCart() throws Exception {
        mockMvc.perform(post("/user/removeFromCart")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("itemId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/cart"));

    }

    @Test
    public void testCheckout() throws Exception {
        mockMvc.perform(post("/user/checkout")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/cart"));


    }
}
