package com.techphantomexample.usermicroservice.messege;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendOrderMessage {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.activemq.destination}")
    private String destination;

    public void sendOrderAsJson(Orders order) throws JsonProcessingException {
        String orderJson = objectMapper.writeValueAsString(order);
        jmsTemplate.convertAndSend(destination, orderJson);
    }
}
