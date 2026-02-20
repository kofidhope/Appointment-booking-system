package com.kofi.booking_system.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClientConfig {

    @Bean
    public WebClient payStackClient(@Value("${paystack.secret-key}") String key){
        return WebClient.builder()
                .baseUrl("https://api.paystack.co")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + key)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
