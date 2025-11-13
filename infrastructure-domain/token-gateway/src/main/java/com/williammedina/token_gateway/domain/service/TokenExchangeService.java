package com.williammedina.token_gateway.domain.service;

import com.williammedina.token_gateway.domain.dto.AccessTokenResponseDTO;
import com.williammedina.token_gateway.domain.dto.TokenRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenExchangeService {

    AccessTokenResponseDTO exchangeCodeForTokens(TokenRequestDTO request, HttpServletResponse response);
    AccessTokenResponseDTO refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);

}
