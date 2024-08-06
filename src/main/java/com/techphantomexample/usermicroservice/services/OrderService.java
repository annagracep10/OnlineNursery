package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Orders;
import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.messege.CancelOrderMessage;
import com.techphantomexample.usermicroservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Orders saveOrder(Orders order) {
        return orderRepository.save(order);
    }

    public List<Orders> findOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public Orders cancelOrder(int orderId) throws JsonProcessingException {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        for (OrderItem item : order.getItems()) {
            productUpdateService.updateProductQuantity(item.getProductId(), item.getProductType(), item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        cancelOrderMessage.sendOrderCancellationMessage(orderId);
        orderRepository.save(order);
        return order;
    }

    public Orders findByRazorpayOrderId(String razorpayOrderId) {
        Orders order = orderRepository.findByRazorpayOrderId(razorpayOrderId);
        return order;
    }

    public Orders findProcessingOrderByUserId(int userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PROCESSING);
    }
}
