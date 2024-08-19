package com.techphantomexample.usermicroservice.api_controller;


import com.techphantomexample.usermicroservice.dto.AuthResponseDto;
import com.techphantomexample.usermicroservice.dto.LoginDto;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.security.JwtGenerator;
import com.techphantomexample.usermicroservice.services.OTPUtil;
import com.techphantomexample.usermicroservice.services.ResetPasswordService;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private UserService userService;
    @Autowired
    private ResetPasswordService resetPasswordService;

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUserEmail(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateResponse> createUser(@RequestBody UserEntity user) {
        String response = userService.createUser(user);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.CREATED.value(), user);
        return new ResponseEntity<>(createResponse, HttpStatus.CREATED);
    }

    @GetMapping("/user-details")
    public UserEntity getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByUserEmail(userEmail);
    }

    int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userService.getUserIdByEmail(userEmail);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Map<String, String>> generateOtp(@RequestParam("email") String email) {
        Map<String, String> response = new HashMap<>();
        try {
            UserEntity user = userRepository.findByUserEmail(email);
            if (user == null) {
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            String otp = OTPUtil.generateOTP();
            resetPasswordService.storeOtpForEmail(email, otp);
            resetPasswordService.sendOtpEmail(email, otp);
            response.put("message", "OTP sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to generate OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam("email") String email,
                                                             @RequestParam("otp") String otp,
                                                             @RequestParam("newPassword") String newPassword) {
        Map<String, String> response = new HashMap<>();

        boolean isOtpValid = resetPasswordService.verifyOtp(email, otp);
        if (!isOtpValid) {
            response.put("message", "Invalid or expired OTP.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String result = resetPasswordService.resetPassword(email, newPassword);
        response.put("message", result);

        return ResponseEntity.ok(response);
    }

}
