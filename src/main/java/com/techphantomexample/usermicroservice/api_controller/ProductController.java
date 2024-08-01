package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.dto.CombinedProductDTO;
import com.techphantomexample.usermicroservice.dto.PlantDTO;
import com.techphantomexample.usermicroservice.dto.PlanterDTO;
import com.techphantomexample.usermicroservice.dto.SeedDTO;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    @GetMapping("/plant/{id}")
    public ResponseEntity<?> getPlantById(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/plant/" + id;
            ResponseEntity<PlantDTO> response = restTemplate.getForEntity(url, PlantDTO.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/plants")
    public ResponseEntity<?> getAllPlants() {
        try {
            String url = productServiceBaseUrl + "/plant";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/planter/{id}")
    public ResponseEntity<?> getPlanterById(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/planter/" + id;
            ResponseEntity<PlanterDTO> response = restTemplate.getForEntity(url, PlanterDTO.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/planters")
    public ResponseEntity<?> getAllPlanters() {
        try {
            String url = productServiceBaseUrl + "/planter";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/seed/{id}")
    public ResponseEntity<?> getSeedById(@PathVariable int id) {
        try {
            String url = productServiceBaseUrl + "/seed/" + id;
            ResponseEntity<SeedDTO> response = restTemplate.getForEntity(url, SeedDTO.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/seeds")
    public ResponseEntity<?> getAllSeeds() {
        try {
            String url = productServiceBaseUrl + "/seed";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response;
        } catch (HttpClientErrorException ex) {
            return handleException(ex);
        }
    }


    private ResponseEntity<CreateResponse> handleException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            CreateResponse createResponse = new CreateResponse(ex.getResponseBodyAsString(), HttpStatus.BAD_REQUEST.value(),null);
            return new ResponseEntity<>(createResponse, HttpStatus.BAD_REQUEST);
        }
        CreateResponse createResponse = new CreateResponse("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(),null);
        return new ResponseEntity<>(createResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
