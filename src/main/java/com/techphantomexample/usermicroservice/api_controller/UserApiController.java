package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserService userService;



    @PostMapping("/login")
    public ResponseEntity<CreateResponse> loginUser(@RequestBody Login login) {
        CreateResponse response = userService.loginUser(login);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PostMapping("/create")
    public ResponseEntity<CreateResponse> createUser(@RequestBody User user) {
        String response = userService.createUser(user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.CREATED.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<CreateResponse> updateUser(@PathVariable int userId, @RequestBody User user) {
        String response = userService.updateUser(userId, user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<CreateResponse> deleteUser(@PathVariable int userId) {
        String response = userService.deleteUser(userId);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), null);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userService.getUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}/cart")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable int userId) {
        Cart cart = userService.getCartByUserId(userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

}
