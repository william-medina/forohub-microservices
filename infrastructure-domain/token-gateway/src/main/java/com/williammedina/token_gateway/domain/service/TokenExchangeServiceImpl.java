package com.williammedina.token_gateway.domain.service;

import com.williammedina.token_gateway.domain.dto.*;
import com.williammedina.token_gateway.infraestructure.client.AuthServerClient;
import com.williammedina.token_gateway.infraestructure.security.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenExchangeServiceImpl implements TokenExchangeService {

    private final AuthServerClient authServerClient;
    private final CookieService cookieService;

    @Value("${auth.client-id}")
    private String clientId;

    @Value("${auth.client-secret}")
    private String clientSecret;


    /**
     * Intercambia el authorization code por tokens (access y refresh).
     * Guarda el refresh_token como cookie HTTP-only.
     */
    @Override
    public AccessTokenResponseDTO exchangeCodeForTokens(TokenRequestDTO request, HttpServletResponse response) {

        String basicAuth = buildBasicAuthHeader(clientId, clientSecret);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", request.code());
        form.add("redirect_uri", request.redirect_uri());

        TokenResponseDTO tokenResponse = authServerClient.getTokensWithCode(basicAuth, form);

        cookieService.addRefreshTokenCookie(response, tokenResponse.refresh_token());

        return AccessTokenResponseDTO.fromAuthServerResponse(tokenResponse);
    }

    /**
     * Usa el refresh token de la cookie para obtener un nuevo access token.
     */
    @Override
    public AccessTokenResponseDTO refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieService.extractRefreshToken(request);
        String basicAuth = buildBasicAuthHeader(clientId, clientSecret);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        TokenResponseDTO tokens = authServerClient.refreshAccessToken(basicAuth, form);

        cookieService.addRefreshTokenCookie(response, tokens.refresh_token());

        return AccessTokenResponseDTO.fromAuthServerResponse(tokens);
    }

    /**
     * Revoca el refresh token y borra la cookie.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieService.getTokenFromCookies(request, "refresh_token").orElse("-");
        String basicAuth = buildBasicAuthHeader(clientId, clientSecret);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", refreshToken);

        authServerClient.logout(basicAuth, form);

        cookieService.removeRefreshTokenCookie(response);
        removeJSessionIdCookie(response);
    }

    private String buildBasicAuthHeader(String clientId, String clientSecret) {
        String credentials = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private void removeJSessionIdCookie(HttpServletResponse response) {
        ResponseCookie deleteJSession = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", deleteJSession.toString());
    }
}