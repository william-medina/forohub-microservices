package com.williammedina.token_gateway.infraestructure.security;

import com.williammedina.token_gateway.infraestructure.exception.AppException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CookieService {

    public static final long REFRESH_TOKEN_EXPIRATION_SECONDS = ( 30 ) * 24 * 60 * 60; // 30 days in seconds


    public String extractRefreshToken(HttpServletRequest request) {
        return getTokenFromCookies(request, "refresh_token")
                .orElseThrow(() -> {
                    log.warn("Refresh token not present in cookies");
                    return new AppException("Refresh token no presente", HttpStatus.UNAUTHORIZED);
                });
    }

    public Optional<String> getTokenFromCookies(HttpServletRequest request, String name) {

        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/token")
                .sameSite("None")
                .partitioned(true)
                .maxAge(REFRESH_TOKEN_EXPIRATION_SECONDS)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/token")
                .sameSite("None")
                .partitioned(true)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());
    }
}
