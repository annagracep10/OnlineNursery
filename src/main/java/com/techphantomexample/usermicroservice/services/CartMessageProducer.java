package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
public class CartMessageProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.activemq.destination}")
    private String destination;

    @Value("${spring.activemq.destination.cancel-order}")
    private String cancelOrderDestination;

    public void sendOrderAsJson(Order order) throws JsonProcessingException {
        String orderJson = objectMapper.writeValueAsString(order);
        jmsTemplate.convertAndSend(destination, orderJson);
    }

    public void sendOrderCancellationMessage(int orderId) throws JsonProcessingException {
        String cancellationMessageJson = objectMapper.writeValueAsString(orderId);
        jmsTemplate.convertAndSend(cancelOrderDestination, cancellationMessageJson);
    }
}
