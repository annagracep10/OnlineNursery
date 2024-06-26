package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
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
public class UserWebController {

    private static final Logger log = LoggerFactory.getLogger(UserWebController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    private  UserService userService;
    @Autowired
    private UserApiController userApiController;

    public UserWebController() {
    }


    public UserWebController(UserRepository userRepository, UserService userService, UserApiController userApiController) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userApiController = userApiController;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute Login login, HttpSession session, Model model) {
        CreateResponse response = userService.loginUser(login);
        if (response.getStatus() == 200) {
            session.setAttribute("user", response.getUser());
            return "redirect:/user/dashboard";
        } else {
            model.addAttribute("error", response.getMessage());
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
            userApiController.createUser(user);
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

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    @PostMapping("/createUser")
    public String saveUser(@ModelAttribute("user") User user,Model model) {
       try {
           userApiController.createUser(user);
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

        User user = userApiController.getUserById(userId).getBody();
        model.addAttribute("user", user);
        return "update_user";
    }

    @PutMapping("/updateUser")
    public String updateUser(int userId, @ModelAttribute User user, Model model) {
        try {
            userApiController.updateUser(userId, user);
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
            userApiController.deleteUser(userId);
            return "redirect:/user/dashboard";
        } catch (UserOperationException e) {
            String errorMessage = e.getMessage();
            log.error("User deletion error: {}", errorMessage);
            model.addAttribute("error", errorMessage);
            return "redirect:/user/dashboard";
        }
    }



}
