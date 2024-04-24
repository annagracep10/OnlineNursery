package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.services.UserOperationException;
import com.techphantomexample.usermicroservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/user")

public class UserController
{
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Read specific user

    @PostMapping
    public ResponseEntity<CreateResponse> createUser(@RequestBody User user) {
        try {
            String response = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CreateResponse(response, HttpStatus.CREATED.value()));
        } catch (UserOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // Read all users in DB
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (!users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(users);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateResponse("No users found", HttpStatus.NOT_FOUND.value()));
            }
        } catch (UserOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Read specific user
    @GetMapping("{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {
        try {
            User user = userService.getUser(userId);
            if (user != null) {
                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateResponse("User not found", HttpStatus.NOT_FOUND.value()));
            }
        } catch (UserOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("{userId}")
    public ResponseEntity<CreateResponse> updateUser(@PathVariable int userId, @RequestBody User user) {
        try {
            String response = userService.updateUser(userId, user);
            return ResponseEntity.status(HttpStatus.OK).body(new CreateResponse(response, HttpStatus.OK.value()));
        } catch (UserOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreateResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<CreateResponse> deleteUser(@PathVariable int userId) {
        try {
            String response = userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new CreateResponse(response, HttpStatus.OK.value()));
        } catch (UserOperationException e) {
            if (e.getMessage().contains("User with ID")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CreateResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
    }

    @PostMapping("{login}")
    public ResponseEntity<?>loginUser(@RequestBody Login login)
    {
        CreateResponse response = userService.loginUser(login);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}

