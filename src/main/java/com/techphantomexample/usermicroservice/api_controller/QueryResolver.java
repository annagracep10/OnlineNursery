package com.techphantomexample.usermicroservice.api_controller;

import com.techphantomexample.usermicroservice.model.Product;
import com.techphantomexample.usermicroservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QueryResolver  {

    @Autowired
    OrderService orderService;

    @QueryMapping
    public List<Product> getTopProducts(@Argument int limit) {
        return orderService.findTopProducts(limit);
    }
}
