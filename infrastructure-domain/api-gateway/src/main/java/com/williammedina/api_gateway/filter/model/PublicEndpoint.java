package com.williammedina.api_gateway.filter.model;


import org.springframework.http.HttpMethod;

public record PublicEndpoint(
        String url,
        HttpMethod method
) {
}
