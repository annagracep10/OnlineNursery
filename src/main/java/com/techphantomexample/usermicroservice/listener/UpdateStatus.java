package com.techphantomexample.usermicroservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.entity.Order;
import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.exception.OrderNotFoundException;
import com.techphantomexample.usermicroservice.repository.OrderRepository;
import com.techphantomexample.usermicroservice.services.ProductUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class UpdateStatus {

    private static final Logger log = LoggerFactory.getLogger(UpdateStatus.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProductUpdateService productUpdateService;

    @Transactional
    @JmsListener(destination = "${spring.activemq.destination.status-update}")
    public void receiveStatusUpdateMessage(String statusUpdateMessageJson) throws JsonProcessingException {
        log.info("Received message: {0}", statusUpdateMessageJson);
        Map<String, Object> messageContent = objectMapper.readValue(statusUpdateMessageJson, Map.class);
        Integer orderId = (Integer) messageContent.get("orderId");
        OrderStatus status = OrderStatus.valueOf((String) messageContent.get("status"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (status == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                productUpdateService.updateProductQuantity(item.getProductId(), item.getProductType(), item.getQuantity());
            }
        }
        order.setStatus(status);
        orderRepository.save(order);
    }


}
