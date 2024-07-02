package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings
class CartMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CartMessageProducer cartMessageProducer;

    private CartDTO cartDTO;

    @BeforeEach
    public void setup() {
        cartDTO = new CartDTO();
    }

    @Test
    public void testSendCartItemsAsJson() throws JsonProcessingException {
        String cartItemsJson = "cartItemsJson";
        when(objectMapper.writeValueAsString(cartDTO)).thenReturn(cartItemsJson);
        cartMessageProducer.sendCartItemsAsJson(cartDTO);
        verify(jmsTemplate, times(1)).convertAndSend(cartItemsJson);
    }
}