package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.Dto.*;
import com.techphantomexample.usermicroservice.model.Cart;
import com.techphantomexample.usermicroservice.model.CartItem;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.services.CartService;
import com.techphantomexample.usermicroservice.services.UserOperationException;
import com.techphantomexample.usermicroservice.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    private  UserService userService;
    @Autowired
    private CartService cartService;
    private CombinedProductDTO combinedProduct;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    public UserController() {
    }

    public UserController(CartService cartService, UserService userService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute Login login, HttpSession session, Model model) {
        try {
            CreateResponse response = userService.loginUser(login);
            if (response.getStatus() == 200) {
                session.setAttribute("user", response.getUser());
                return "redirect:/user/dashboard";
            } else {
                model.addAttribute("error", response.getMessage());
                return "login";
            }
        } catch (UserOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/user/login"; // Redirect to login page
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user);
            return "redirect:/user/login";
        } catch (UserOperationException e) {
            String errorMessage = e.getMessage();
            log.error("User registration error: {}", errorMessage);
            model.addAttribute("error", errorMessage);
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        if ("ADMIN".equals(user.getUserRole())) {
            model.addAttribute("listOfUsers", userService.getAllUsers());
            return "dashboard"; // Admin dashboard view
        } else {
            return "redirect:/user/products"; // Non-admin product list view
        }
    }
    @GetMapping("/products")
    public String showProducts(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        String url = productServiceBaseUrl;
        CombinedProductDTO combinedProduct = restTemplate.getForObject(url + "/products", CombinedProductDTO.class);
        model.addAttribute("combinedProduct", combinedProduct);
        model.addAttribute("user", user);
        return "product-list";
    }

    @GetMapping("/newProductForm")
    public String newProductForm(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);
        switch (category.toLowerCase()) {
            case "plant":
                model.addAttribute("plant", new PlantDTO());
                log.info("System.out.println(inside switch);");
                break;
            case "planter":
                model.addAttribute("planter", new PlanterDTO());
                break;
            case "seed":
                model.addAttribute("seed", new SeedDTO());
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }
        return "new_product";
    }

    @PostMapping("/createProduct")
    public String saveProduct(@RequestParam("category") String category, PlantDTO plant, PlanterDTO planter, SeedDTO seed, Model model) {
        String url = productServiceBaseUrl;
        try {
            switch (category.toLowerCase()) {
                case "plant":
                    log.info("Processing plant");
                    model.addAttribute("plant", plant);
                    url += "/plant";
                    restTemplate.postForObject(url, plant, PlantDTO.class);
                    break;
                case "planter":
                    log.info("Processing planter");
                    model.addAttribute("planter", planter);
                    url += "/planter";
                    restTemplate.postForObject(url, planter, PlanterDTO.class);
                    break;
                case "seed":
                    log.info("Processing seed");
                    model.addAttribute("seed", seed);
                    url += "/seed";
                    restTemplate.postForObject(url, seed, SeedDTO.class);
                    break;
                default:
                    log.info("Invalid product category");
                    model.addAttribute("error", "Invalid product category");
                    return "new_product";
            }
            return "redirect:/user/products";
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e, model);
            model.addAttribute("category", category);
            return  "new_product"; // or whatever view you want to return for errors
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("category", category);
            return "new_product"; // or whatever view you want to return for errors
        }
    }

    @GetMapping("/showFormForUpdateProduct/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        String url = productServiceBaseUrl;
        switch (category.toLowerCase()) {
            case "plant":
                url += "/plant/" + id;
                PlantDTO plant = restTemplate.getForObject(url, PlantDTO.class);
                model.addAttribute("plant", plant);
                model.addAttribute("category", "plant");
                break;
            case "planter":
                url += "/planter/" + id;
                PlanterDTO planter = restTemplate.getForObject(url, PlanterDTO.class);
                model.addAttribute("planter", planter);
                model.addAttribute("category", "planter");
                break;
            case "seed":
                url += "/seed/" + id;
                SeedDTO seed = restTemplate.getForObject(url, SeedDTO.class);
                model.addAttribute("seed", seed);
                model.addAttribute("category", "seed");
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }
        return "update_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@RequestParam("category") String category, PlantDTO plant, PlanterDTO planter, SeedDTO seed, Model model) {
        String url = productServiceBaseUrl;
        try {
            switch (category.toLowerCase()) {
                case "plant":
                    url += "/plant/" + plant.getId();
                    restTemplate.put(url, plant);
                    break;
                case "planter":
                    url += "/planter/" + planter.getId();
                    restTemplate.put(url, planter);
                    break;
                case "seed":
                    url += "/seed/" + seed.getId();
                    restTemplate.put(url, seed);
                    break;
                default:
                    model.addAttribute("error", "Invalid product category");
                    return "update_product";
            }
            return "redirect:/user/products";
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e, model);
            model.addAttribute("category", category);
            return "update_product";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("category", category);
            return "update_product";
        }
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        String url = productServiceBaseUrl;
        switch (category.toLowerCase()) {
            case "plant":
                url += "/plant/" + id;
                restTemplate.delete(url);
                break;
            case "planter":
                url += "/planter/" + id;
                restTemplate.delete(url);
                break;
            case "seed":
                url += "/seed/" + id;
                restTemplate.delete(url);
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "product_list";
        }
        return "redirect:/user/products";
    }

    private void handleHttpClientErrorException(HttpClientErrorException e, Model model) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            model.addAttribute("error", "Validation error: " + e.getMessage());

        } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            model.addAttribute("error", "Product not found: " + e.getMessage());
        } else {
            model.addAttribute("error", "An error occurred while updating the product: " + e.getResponseBodyAsString());
        }
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    @PostMapping("/createUser")
    public String saveUser(@ModelAttribute("user") User user,Model model) {
       try {
           userService.createUser(user);
           return "redirect:/user/dashboard";
       } catch (UserOperationException e) {
           String errorMessage = e.getMessage();
           log.error("User creation error: {}", errorMessage);
           model.addAttribute("error", errorMessage);
           return "new_user";
       }
    }

    @GetMapping("/showFormForUpdate/{userId}")
    public String showFormForUpdate(@PathVariable(value = "userId") int userId, Model model) {

        User user = userService.getUser(userId);
        model.addAttribute("user", user);
        return "update_user";
    }

    @PutMapping("/updateUser")
    public String updateUser(int userId, @ModelAttribute User user, Model model) {
        try {
            userService.updateUser(userId, user);
            return "redirect:/user/dashboard";
        } catch (UserOperationException e) {
            String errorMessage = e.getMessage();
            log.error("User update error: {}", errorMessage);
            model.addAttribute("error", errorMessage);
            return "update_user";
        }
    }

    @GetMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable int userId,Model model) {
        try {
            userService.deleteUser(userId);
            return "redirect:/user/dashboard";
        } catch (UserOperationException e) {
            String errorMessage = e.getMessage();
            log.error("User deletion error: {}", errorMessage);
            model.addAttribute("error", errorMessage);
            return "redirect:/user/dashboard";
        }
    }

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
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartService.checkout(user.getUserId());
        }
        return "redirect:/user/cart";
    }


}
