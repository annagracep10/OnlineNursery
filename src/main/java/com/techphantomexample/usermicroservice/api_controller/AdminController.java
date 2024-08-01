package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.dto.PlantDTO;
import com.techphantomexample.usermicroservice.dto.PlanterDTO;
import com.techphantomexample.usermicroservice.dto.SeedDTO;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.ProductResponse;
import com.techphantomexample.usermicroservice.services.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<CreateResponse> deleteUser(@PathVariable int userId) {
        String response = userService.deleteUser(userId);
        CreateResponse createResponse = new CreateResponse(response, HttpStatus.OK.value(), null);
        return new ResponseEntity<>(createResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable int userId) {
        UserEntity user = userService.getUser(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/plant")
    public ResponseEntity<?> createPlant(@RequestBody PlantDTO plant) {
        try {
            String url = productServiceBaseUrl + "/plant";
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(url, plant, ProductResponse.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/plant/{id}")
    public ResponseEntity<?> updatePlant(@PathVariable int id, @RequestBody PlantDTO plant) {
        try {
            String url = productServiceBaseUrl + "/plant/" + id;
            restTemplate.put(url, plant);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/plant/{id}")
    public ResponseEntity<?> deletePlant(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/plant/" + id;
            restTemplate.delete(url);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/planter")
    public ResponseEntity<?> createPlanter(@RequestBody PlanterDTO planter) {
        try {
            String url = productServiceBaseUrl + "/planter";
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(url, planter, ProductResponse.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/planter/{id}")
    public ResponseEntity<?> updatePlanter(@PathVariable int id, @RequestBody PlanterDTO planter) {
        try {
            String url = productServiceBaseUrl + "/planter/" + id;
            restTemplate.put(url, planter);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/planter/{id}")
    public ResponseEntity<?> deletePlanter(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/planter/" + id;
            restTemplate.delete(url);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/seed")
    public ResponseEntity<?> createSeed(@RequestBody SeedDTO seed) {
        try {
            String url = productServiceBaseUrl + "/seed";
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(url, seed, ProductResponse.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/seed/{id}")
    public ResponseEntity<?> updateSeed(@PathVariable int id, @RequestBody SeedDTO seed) {
        try {
            String url = productServiceBaseUrl + "/seed/" + id;
            restTemplate.put(url, seed);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/seed/{id}")
    public ResponseEntity<?> deleteSeed(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/seed/" + id;
            restTemplate.delete(url);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    private ResponseEntity<ProductResponse> handleException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            ProductResponse createResponse = new ProductResponse(ex.getResponseBodyAsString(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(createResponse, HttpStatus.BAD_REQUEST);
        }
        ProductResponse createResponse = new ProductResponse("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(createResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
