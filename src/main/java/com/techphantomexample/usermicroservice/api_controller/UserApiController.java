package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.dto.ChangePasswordRequest;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);
    @Autowired
    private UserService userService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<CreateResponse> updateUser(@PathVariable int userId, @RequestBody UserEntity user) {
        String response = userService.updateUser(userId, user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String response = userService.changePassword(changePasswordRequest.getUserId(), changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());
        if (response.equals("User not found.") || response.equals("Current password is incorrect.")) {
            log.info("Current password is incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

}
