package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeedRequest {
    private String name;
    private String description;
    private double price;
    private String category;
    private int quantity;
    private String seedType;
    private int germinationTime;
    private String season;
}
