package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public CreateResponse loginUser(@RequestBody Login login) {
        CreateResponse response = userService.loginUser(login);
        return response;
    }

    @PostMapping("/create")
    public CreateResponse createUser(@RequestBody User user) {
        String response = userService.createUser(user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(),user);
        return createResponse;
    }

    @PutMapping("/update/{userId}")
    public CreateResponse updateUser(@PathVariable int userId, @RequestBody User user) {
        String response = userService.updateUser(userId, user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), user);
        return createResponse;
    }

    @DeleteMapping("/delete/{userId}")
    public CreateResponse deleteUser(@PathVariable int userId) {
        String response = userService.deleteUser(userId);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(),null);
        return createResponse;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userService.getUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
