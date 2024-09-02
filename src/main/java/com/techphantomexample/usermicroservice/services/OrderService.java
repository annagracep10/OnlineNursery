package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.entity.Orders;
import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.exception.NotFoundException;
import com.techphantomexample.usermicroservice.messege.CancelOrderMessage;
import com.techphantomexample.usermicroservice.model.Product;
import com.techphantomexample.usermicroservice.repository.OrderItemRepository;
import com.techphantomexample.usermicroservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    @Transactional(readOnly = true)
    public List<Product> findTopProducts(int limit) {
        List<Object[]> results = orderItemRepository.findTopProducts();
        log.info(results.toString());
        return orderItemRepository.findTopProducts().stream()
                .limit(limit)
                .map(result -> new Product(
                        (Integer) result[0],
                        (String) result[1],
                        ((Long) result[2]).intValue()
                ))
                .collect(Collectors.toList());
    }
}
