package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanterRequest {

    private String name;
    private String description;
    private double price;
    private String category;
    private int quantity;
    private String material;
    private String dimensions;
    private String color;
}
