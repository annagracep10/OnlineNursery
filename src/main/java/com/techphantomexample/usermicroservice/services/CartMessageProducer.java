package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
public class CartMessageProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendCartItemsAsJson(CartDTO cartDTO) throws JsonProcessingException {
            String cartItemsJson = objectMapper.writeValueAsString(cartDTO);
            jmsTemplate.convertAndSend(cartItemsJson);

    }
}
