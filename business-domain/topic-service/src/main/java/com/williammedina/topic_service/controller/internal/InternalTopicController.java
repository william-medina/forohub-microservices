package com.williammedina.topic_service.controller.internal;

import com.williammedina.topic_service.domain.topic.dto.InputTopicStatusDTO;
import com.williammedina.topic_service.domain.topic.dto.TopicDetailsDTO;
import com.williammedina.topic_service.domain.topic.dto.TopicSummaryDTO;
import com.williammedina.topic_service.domain.topic.dto.UserTopicCountDTO;
import com.williammedina.topic_service.domain.topic.service.InternalTopicService;
import com.williammedina.topic_service.domain.topicfollow.dto.UserFollowedTopicCountDTO;
import com.williammedina.topic_service.domain.topicfollow.service.InternalTopicFollowService;
import com.williammedina.topic_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Hidden
@RestController
@RequestMapping(path = "/internal/topic", produces = "application/json")
@Tag(name = "Internal Topic", description = "Endpoints internos del servicio de tópicos usados para la comunicación entre microservicios")
@AllArgsConstructor
public class InternalTopicController {

    private final InternalTopicService topicService;
    private final InternalTopicFollowService topicFollowService;

    @Operation(
            summary = "[INTERNAL] Cambiar estatus de un tópico",
            description = "Permite cambiar el estatus (ACTIVE | CLOSED) de tópico existente por su ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Estatus del tópico cambiado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping("/{topicId}/status")
    public ResponseEntity<TopicDetailsDTO> changeTopicStatus(@PathVariable Long topicId, @RequestBody @Valid InputTopicStatusDTO data) {
        TopicDetailsDTO topic = topicService.changeTopicStatus(data, topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(
            summary = "[INTERNAL] Obtener un tópico resumido por ID",
            description = "Permite obtener un tópico específico por su ID, devolviendo solo la información básica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópico resumido recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con microservicios externos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/summary/{topicId}")
    public ResponseEntity<TopicSummaryDTO> getTopicSummaryById(@PathVariable Long topicId) {
        TopicSummaryDTO topic = topicService.getTopicSummaryById(topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(
            summary = "[INTERNAL] Obtener la cantidad de tópicos creados por un usuario",
            description = "Devuelve un objeto con la cantidad de tópicos que un usuario ha creado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de tópicos recuperada exitosamente"),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<UserTopicCountDTO> getTopicCountByUser(@PathVariable Long userId) {
        UserTopicCountDTO countDTO = topicService.getTopicCountByUser(userId);
        return ResponseEntity.ok(countDTO);
    }

    @Operation(
            summary = "[INTERNAL] Obtener la cantidad de tópicos seguidos por un usuario",
            description = "Devuelve un objeto con la cantidad de tópicos que un usuario está siguiendo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de tópicos seguidos recuperada exitosamente"),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/user/{userId}/followed/count")
    public ResponseEntity<UserFollowedTopicCountDTO> getUserFollowedTopicCount(@PathVariable Long userId) {
        UserFollowedTopicCountDTO countDTO = topicFollowService.getFollowedTopicCountByUser(userId);
        return ResponseEntity.ok(countDTO);
    }


    @Operation(
            summary = "[INTERNAL] Exportar todos los tópicos para sincronización inicial",
            description = "Devuelve una lista completa de todos los tópicos (con detalles, curso, autor, respuestas y seguidores) "
                    + "para uso interno del servicio de lectura (topic-read-service). "
                    + "Este endpoint está diseñado para sincronización inicial y no debe usarse frecuentemente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tópicos exportada exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No existen tópicos para exportar", content = @Content),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible o error al consultar microservicios externos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @GetMapping("/export")
    public ResponseEntity<List<TopicDetailsDTO>> exportAllTopics() {
        List<TopicDetailsDTO> topics = topicService.exportAllTopics();

        if (topics == null || topics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(topics);
    }

}
