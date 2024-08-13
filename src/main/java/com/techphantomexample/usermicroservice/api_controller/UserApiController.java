package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.dto.ChangePasswordRequest;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private AuthController authController;

    @PutMapping("/update/{userId}")
    public ResponseEntity<CreateResponse> updateUser(@PathVariable int userId, @RequestBody UserEntity user) {
        String response = userService.updateUser(userId, user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        int userId = authController.getCurrentUserId();
        Map<String, String> response = new HashMap<>();
        String result = userService.changePassword(userId, changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());

        response.put("message", result);

        if (result.equals("User not found.") || result.equals("Current password is incorrect.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
