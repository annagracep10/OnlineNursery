package com.techphantomexample.usermicroservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String productType;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

}
