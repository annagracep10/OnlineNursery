package com.techphantomexample.usermicroservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class CartWebController {

    private static final Logger log = LoggerFactory.getLogger(CartWebController.class);
    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Cart cart = cartService.getCartByUserId(user.getUserId());
            model.addAttribute("cart", cart != null ? cart : new Cart());
            model.addAttribute("user",user);
        } else {
            model.addAttribute("cart", new Cart());
            model.addAttribute("user",user);
        }
        return "cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@ModelAttribute CartItem cartItem, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartService.addItemToCart(user.getUserEmail(), cartItem);
        }
        return "redirect:/user/products";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam("itemId") int itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartService.removeItemFromCart(user.getUserId(), itemId);
        }
        return "redirect:/user/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) throws JsonProcessingException {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartService.checkout(user.getUserId());
        }
        return "redirect:/user/cart";
    }
}
