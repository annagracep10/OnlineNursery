package com.techphantomexample.usermicroservice.messege;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class CancelOrderMessage {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.activemq.destination.cancel-order}")
    private String cancelOrderDestination;

    public void sendOrderCancellationMessage(int orderId) throws JsonProcessingException {
        String cancellationMessageJson = objectMapper.writeValueAsString(orderId);
        jmsTemplate.convertAndSend(cancelOrderDestination, cancellationMessageJson);
    }
}
