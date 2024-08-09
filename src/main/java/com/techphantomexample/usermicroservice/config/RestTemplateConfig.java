package com.techphantomexample.usermicroservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();

        List<MappingJackson2HttpMessageConverter> converters = restTemplate.getMessageConverters()
                .stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .toList();

        if (converters.isEmpty()) {
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        }

        return restTemplate;
    }
}
