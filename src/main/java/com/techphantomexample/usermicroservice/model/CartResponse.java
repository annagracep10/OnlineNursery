package com.techphantomexample.usermicroservice.model;

import com.techphantomexample.usermicroservice.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private String message;
    private int status;
    private Object cart;
}
