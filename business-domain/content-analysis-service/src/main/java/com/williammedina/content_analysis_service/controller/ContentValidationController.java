package com.williammedina.content_analysis_service.controller;

import com.williammedina.content_analysis_service.domain.contentvalidation.service.ContentValidationService;
import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationRequest;
import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationResponse;
import com.williammedina.content_analysis_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/validation", produces = "application/json")
@Tag(name = "Content Validation", description = "Endpoints para validar contenido de títulos, descripciones y nombres de usuario mediante IA.")
@AllArgsConstructor
public class ContentValidationController {

    private final ContentValidationService validationService;

    @Operation(
            summary = "Validar contenido de texto",
            description = "Valida un texto (título o descripción de un tópico) para determinar si contiene contenido inapropiado, spam, lenguaje ofensivo o irrelevante.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Texto validado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/content")
    public Mono<ContentValidationResponse> validateContent(@RequestBody @Valid ContentValidationRequest request) {
        return validationService.validateContent(request.text());
    }

    @Operation(
            summary = "Validar nombre de usuario",
            description = "Valida un nombre de usuario para detectar contenido inapropiado, ofensivo, spam o nombres sin sentido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nombre de usuario validado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Error al validar el contenido con el servicio de IA.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/username")
    public Mono<ContentValidationResponse> validateUsername(@RequestBody @Valid ContentValidationRequest request) {
        return validationService.validateUsername(request.text());
    }
}
