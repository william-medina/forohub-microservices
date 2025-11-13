package com.williammedina.topic_service.infrastructure.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient contentValidationWebClient(@LoadBalanced WebClient.Builder builder) {
        return builder.baseUrl("http://content-analysis-service/validation").build();
    }
}
