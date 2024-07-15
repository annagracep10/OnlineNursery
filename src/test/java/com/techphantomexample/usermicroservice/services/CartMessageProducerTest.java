package com.techphantomexample.usermicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.dto.CartDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartMessageProducerTest {

    @Mock
    JmsTemplate jmsTemplate;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    CartMessageProducer cartMessageProducer;

    @Test
    void testSendCartItemsAsJson_Success() throws JsonProcessingException {
        CartDTO cartDTO = new CartDTO();
        String cartItemsJson = "";
        when(objectMapper.writeValueAsString(cartDTO)).thenReturn(cartItemsJson);

        cartMessageProducer.sendCartItemsAsJson(cartDTO);

        verify(objectMapper, times(1)).writeValueAsString(cartDTO);
        verify(jmsTemplate, times(1)).convertAndSend(cartItemsJson);
    }

    @Test
    void testSendCartItemsAsJson_JsonProcessingException() throws JsonProcessingException {
        CartDTO cartDTO = new CartDTO();
        when(objectMapper.writeValueAsString(cartDTO)).thenThrow(new JsonProcessingException("Test Exception") {});

        assertThrows(JsonProcessingException.class, () -> cartMessageProducer.sendCartItemsAsJson(cartDTO));

        verify(objectMapper, times(1)).writeValueAsString(cartDTO);
        verify(jmsTemplate, never()).convertAndSend(anyString());
    }




}