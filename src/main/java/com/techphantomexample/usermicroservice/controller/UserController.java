package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.services.UserOperationException;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    private  UserService userService;

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
    public String showDashboard(Model model) {
        model.addAttribute("listOfUsers", userService.getAllUsers());
        return "dashboard";
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    @PostMapping("/createUser")
    public String saveUser(@ModelAttribute("user") User user) {
        // save employee to database
        userService.createUser(user);
        return "redirect:/user/dashboard";
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
