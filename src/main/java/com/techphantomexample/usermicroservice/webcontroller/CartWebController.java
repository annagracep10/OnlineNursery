package com.techphantomexample.usermicroservice.webcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.CartItem;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.UserService;
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
    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Cart cart = userService.getCartByUserId(user.getUserId());
        model.addAttribute("cart", cart != null ? cart : new Cart());
        model.addAttribute("user",user);
        return "cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@ModelAttribute CartItem cartItem, HttpSession session) {
        User user = (User) session.getAttribute("user");
        cartService.addItemToCart(user.getUserId(), cartItem);
        log.info("Item added to cart");
        return "redirect:/user/products";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam("itemId") int itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        cartService.removeItemFromCart(user.getUserId(), itemId);
        log.info("Item removed from cart");
        return "redirect:/user/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) throws JsonProcessingException {
        User user = (User) session.getAttribute("user");
        cartService.checkout(user.getUserId());
        log.info("Order Checked out");
        return "redirect:/user/cart";
    }


}
