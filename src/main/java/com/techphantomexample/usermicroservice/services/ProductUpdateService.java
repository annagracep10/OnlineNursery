package com.techphantomexample.usermicroservice.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Service
public class ProductUpdateService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.base-url}")
    private String productServiceBaseUrl;

    public void updateProductQuantity(int productId, String productType, int quantityToSubtract) {
        String url = productServiceBaseUrl + "/" + productType + "/" + productId + "/quantity";
        Map<String, Integer> updateQuantityMap = new HashMap<>();
        updateQuantityMap.put("quantityToSubtract", quantityToSubtract);
        restTemplate.put(url, updateQuantityMap);
    }
}
