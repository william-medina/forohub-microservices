package com.williammedina.token_gateway.controller;

import com.williammedina.token_gateway.domain.dto.AccessTokenResponseDTO;
import com.williammedina.token_gateway.domain.dto.TokenRequestDTO;
import com.williammedina.token_gateway.domain.service.TokenExchangeService;
import com.williammedina.token_gateway.infraestructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Tag(name = "Token Gateway", description = "Maneja el intercambio de tokens entre el frontend y el Auth Server.")
public class TokenExchangeController {

    private final TokenExchangeService tokenExchangeService;

    @Operation(
            summary = "Intercambia el authorization_code por tokens",
            description = "Recibe el código de autorización del frontend y obtiene el access_token desde el Auth Server. El refresh_token se almacena en cookie HTTP-only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens successfully obtained"),
                    @ApiResponse(responseCode = "400", description = "Invalid grant or request", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @PostMapping("/exchange")
    public ResponseEntity<AccessTokenResponseDTO> exchangeCodeForTokens(@RequestBody TokenRequestDTO request, HttpServletResponse response) {
        return ResponseEntity.ok(tokenExchangeService.exchangeCodeForTokens(request, response));
    }

    @Operation(
            summary = "Refresca el access_token",
            description = "Usa el refresh_token almacenado en cookie para obtener un nuevo access_token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "401", description = "Refresh token missing or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(tokenExchangeService.refreshAccessToken(request, response));
    }

    @Operation(
            summary = "Cerrar sesión (logout)",
            description = "Elimina la cookie que contiene el refresh_token.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Logged out successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        tokenExchangeService.logout(request,response);
        return ResponseEntity.noContent().build();
    }
}
