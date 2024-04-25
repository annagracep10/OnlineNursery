package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.services.UserOperationException;
import com.techphantomexample.usermicroservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepository userRepository;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute Login login, Model model) {
        try {
            CreateResponse response = userService.loginUser(login);
            if (response.getStatus() == 200) {
                return "redirect:/user/dashboard"; // Redirect to dashboard if login successful
            } else {
                // If login unsuccessful, display the error message from the CreateResponse object
                model.addAttribute("error", response.getMessage());
                return "login"; // Return to login page with error message
            }
        } catch (UserOperationException e) {
            // If an exception occurs during login, use the error message from the exception
            model.addAttribute("error", e.getMessage());
            return "login"; // Return to login page with error message
        }
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user); // Implement user registration logic
            return "redirect:/user/login"; // Redirect to login page after successful registration
        } catch (UserOperationException e) {
            String errorMessage = e.getMessage();
            // Log the error message for debugging
            log.error("User registration error: {}", errorMessage);
            // Add the error message to the model
            model.addAttribute("error", errorMessage);
            // Return to registration page with error message
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "dashboard";
    }

    @PutMapping("/user/{userId}")
    public String updateUser(@PathVariable int userId, @ModelAttribute User user) {
        try {
            // Implement user update logic
            return "redirect:/user/dashboard";
        } catch (UserOperationException e) {
            // Handle exception, display error message if necessary
            return "error";
        }
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable int userId) {
        try {
            // Implement user deletion logic
            return "redirect:/user/dashboard";
        } catch (UserOperationException e) {
            // Handle exception, display error message if necessary
            return "error";
        }
    }
}
