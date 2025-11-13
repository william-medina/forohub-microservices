package com.williammedina.token_gateway.infraestructure.client;

import com.williammedina.token_gateway.domain.dto.TokenResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-server", path = "/oauth2")
public interface AuthServerClient {


    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    TokenResponseDTO getTokensWithCode(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody MultiValueMap<String, String> form
    );

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    TokenResponseDTO refreshAccessToken(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody MultiValueMap<String, String> form
    );

    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    void logout(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody MultiValueMap<String, String> form
    );

}
