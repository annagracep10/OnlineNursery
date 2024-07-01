package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.Dto.CartDTO;
import com.techphantomexample.usermicroservice.services.CartMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartMessageProducerTest {

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
