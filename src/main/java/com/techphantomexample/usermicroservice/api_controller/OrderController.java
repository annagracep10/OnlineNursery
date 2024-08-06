package com.techphantomexample.usermicroservice.api_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Orders;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private  AuthController authController;

    @GetMapping()
    public ResponseEntity<List<Orders>> getOrdersByUserId() {
        int userId = authController.getCurrentUserId();
        List<Orders> orders = orderService.findOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Orders> cancelOrder(@PathVariable int orderId)  {
        try {
            Orders canceledOrder = orderService.cancelOrder(orderId);
            return new ResponseEntity<>(canceledOrder, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
