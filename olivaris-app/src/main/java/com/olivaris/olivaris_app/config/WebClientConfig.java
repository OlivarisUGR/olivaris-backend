package com.olivaris.olivaris_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Configuration
public class WebClientConfig {

    @Bean
    public Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Component for the SIGPAC WebClient
    @Bean
    public WebClient sigpacApiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://sigpac-hubcloud.es")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
