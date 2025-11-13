package com.williammedina.auth_server.infraestructure.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final AppProperties appProperties;

    public GlobalControllerAdvice(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @ModelAttribute("frontendUrl")
    public String frontendUrl() {
        return appProperties.getFrontendUrl();
    }
}
