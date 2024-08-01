package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserService userService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<CreateResponse> updateUser(@PathVariable int userId, @RequestBody UserEntity user) {
        String response = userService.updateUser(userId, user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

}
