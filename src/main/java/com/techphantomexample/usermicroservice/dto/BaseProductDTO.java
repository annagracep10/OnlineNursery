package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseProductDTO {

    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int quantity;


}
