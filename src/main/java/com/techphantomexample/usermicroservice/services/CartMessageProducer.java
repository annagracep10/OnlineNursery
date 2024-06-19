package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import com.techphantomexample.usermicroservice.model.Cart;
import jakarta.jms.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
public class CartMessageProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendCartItemsAsJson(CartDTO cartDTO) {
        try {
            String cartItemsJson = objectMapper.writeValueAsString(cartDTO);
            jmsTemplate.convertAndSend(cartItemsJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
