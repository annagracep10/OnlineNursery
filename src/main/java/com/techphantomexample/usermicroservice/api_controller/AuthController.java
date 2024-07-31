package com.techphantomexample.usermicroservice.api_controller;


import com.techphantomexample.usermicroservice.dto.AuthResponseDto;
import com.techphantomexample.usermicroservice.dto.LoginDto;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.security.JwtGenerator;
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

}
