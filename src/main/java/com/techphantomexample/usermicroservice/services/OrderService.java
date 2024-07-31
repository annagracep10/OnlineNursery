package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Order;
import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.messege.CancelOrderMessage;
import com.techphantomexample.usermicroservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CancelOrderMessage cancelOrderMessage;

    @Autowired
    ProductUpdateService productUpdateService;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> findOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found for user with ID: " + userId);
        }
        return orders;
    }

    public Order cancelOrder(int orderId) throws JsonProcessingException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        for (OrderItem item : order.getItems()) {
            productUpdateService.updateProductQuantity(item.getProductId(), item.getProductType(), item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        cancelOrderMessage.sendOrderCancellationMessage(orderId);
        orderRepository.save(order);
        return order;
    }


}
