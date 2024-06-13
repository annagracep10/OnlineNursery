package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.Dto.*;
import com.techphantomexample.usermicroservice.config.RestTemplateConfig;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.services.UserOperationException;
import com.techphantomexample.usermicroservice.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    private  UserService userService;
    private CombinedProduct combinedProduct;
    @Autowired
    private RestTemplate restTemplate;

    public UserController(UserService userService) {
        this.userService = userService;
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
        CombinedProduct combinedProduct = restTemplate.getForObject("http://localhost:9091/product/products",CombinedProduct.class);
        model.addAttribute("combinedProduct", combinedProduct);
        model.addAttribute("user", user);
        return "product-list";
    }

    @GetMapping("/newProductForm")
    public String newProductForm(@RequestParam("category") String category, Model model) {
        model.addAttribute("category", category);
        System.out.println("inside the get");
        System.out.println(category.toLowerCase());
        switch (category.toLowerCase()) {
            case "plant":
                model.addAttribute("plant", new Plant());
                log.info("System.out.println(inside switch);");
                break;
            case "planter":
                model.addAttribute("planter", new Planter());
                break;
            case "seed":
                model.addAttribute("seed", new Seed());
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }
        System.out.println("displaying page");
//        System.out.println("Product 1 :"+plant);
//        System.out.println("Product 2 :"+planter);
//        System.out.println("Product 3 :"+seed);// Add category to the model
        return "new_product";
    }

    @PostMapping("/createProduct")
    public String saveProduct(@RequestParam("category") String category, Plant plant , Planter planter , Seed seed, Model model) {
        log.info("Received category: " + category);  // Debug logging
        String url = "";
        System.out.println(plant);
        System.out.println(planter);
        System.out.println(seed);
        switch (category.toLowerCase()) {
            case "plant":
                log.info("Processing plant");
                model.addAttribute("plant", plant);
                url = "http://localhost:9091/product/plant";
                restTemplate.postForObject(url, plant, Plant.class);
                return "redirect:/user/products";
            case "planter":
                log.info("Processing planter");
                model.addAttribute("planter", planter);
                url = "http://localhost:9091/product/planter";
                restTemplate.postForObject(url, planter, Planter.class);
                return "redirect:/user/products";
            case "seed":
                log.info("Processing seed");
                model.addAttribute("seed", seed);
                url = "http://localhost:9091/product/seed";
                restTemplate.postForObject(url, seed, Seed.class);
                return "redirect:/user/products";
            default:
                log.info("here");
                model.addAttribute("error", "Invalid product category");
                return "new_product";
        }

    }

    @GetMapping("/showFormForUpdateProduct/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        switch (category.toLowerCase()) {
            case "plant":
                Plant plant = restTemplate.getForObject("http://localhost:9091/product/plant/" + id, Plant.class);
                model.addAttribute("plant", plant);
                model.addAttribute("category", "plant");
                break;
            case "planter":
                Planter planter = restTemplate.getForObject("http://localhost:9091/product/planter/" + id, Planter.class);
                model.addAttribute("planter", planter);
                model.addAttribute("category", "planter");
                break;
            case "seed":
                Seed seed = restTemplate.getForObject("http://localhost:9091/product/seed/" + id, Seed.class);
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
    public String updateProduct(@RequestParam("category") String category, Plant plant, Planter planter, Seed seed, Model model) {
        String url = "";
        switch (category.toLowerCase()) {
            case "plant":
                url = "http://localhost:9091/product/plant/" + plant.getId();
                restTemplate.put(url, plant);
                return "redirect:/user/products";
            case "planter":
                url = "http://localhost:9091/product/planter/" + planter.getId();
                restTemplate.put(url, planter);
                return "redirect:/user/products";
            case "seed":
                url = "http://localhost:9091/product/seed/" + seed.getId();
                restTemplate.put(url, seed);
                return "redirect:/user/products";
            default:
                model.addAttribute("error", "Invalid product category");
                return "update_product";
        }
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") Long id, @RequestParam("category") String category, Model model) {
        String url = "";
        switch (category.toLowerCase()) {
            case "plant":
                url = "http://localhost:9091/product/plant/" + id;
                restTemplate.delete(url);
                break;
            case "planter":
                url = "http://localhost:9091/product/planter/" + id;
                restTemplate.delete(url);
                break;
            case "seed":
                url = "http://localhost:9091/product/seed/" + id;
                restTemplate.delete(url);
                break;
            default:
                model.addAttribute("error", "Invalid product category");
                return "product_list";
        }
        return "redirect:/user/products";
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
            log.error("User updation error: {}", errorMessage);
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




}
