package com.williammedina.reply_service.controller.internal;

import com.williammedina.reply_service.domain.reply.dto.ReplyCountDTO;
import com.williammedina.reply_service.domain.reply.dto.ReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.UserReplyCountDTO;
import com.williammedina.reply_service.domain.reply.service.InternalReplyService;
import com.williammedina.reply_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Hidden
@RestController
@RequestMapping(path = "/internal/reply", produces = "application/json")
@Tag(name = "Internal Reply", description = "Endpoints internos del servicio de repuestas de los tópicos usados para la comunicación entre microservicios.")
@AllArgsConstructor
public class InternalReplyController {

    private final InternalReplyService replyService;

    @Operation(
            summary = "[INTERNAL] Obtener todas las respuestas de un tópico",
            description = "Recupera todas las respuestas asociadas a un tópico específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuestas recuperadas exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ReplyDTO>> getAllRepliesByTopic(@PathVariable Long topicId) {
        List<ReplyDTO> replies = replyService.getAllRepliesByTopic(topicId);
        return ResponseEntity.ok(replies);
    }

    @Operation(
            summary = "[INTERNAL] Obtener la cantidad de respuestas de un tópico",
            description = "Devuelve el número total de respuestas activas (no eliminadas) asociadas a un tópico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de respuestas obtenida exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
            }
    )
    @GetMapping("/topic/{topicId}/count")
    public ResponseEntity<ReplyCountDTO> getReplyCountByTopic(@PathVariable Long topicId) {
        ReplyCountDTO replyCount = replyService.getReplyCountByTopic(topicId);
        return ResponseEntity.ok(replyCount);
    }

    @Operation(
            summary = "[INTERNAL] Obtener cantidad de respuestas por IDs de tópicos",
            description = "Devuelve la cantidad de respuestas asociadas a una lista de tópicos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conteo de respuestas recuperado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Lista de IDs inválida", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/count/batch")
    public ResponseEntity<List<ReplyCountDTO>> getReplyCountsByTopicIds(@RequestParam("ids") List<Long> topicIds) {
        List<ReplyCountDTO> counts = replyService.getReplyCountsByTopicIds(topicIds);
        return ResponseEntity.ok(counts);
    }

    @Operation(
            summary = "[INTERNAL] Obtener cantidad de respuestas hechas por un usuario",
            description = "Devuelve un objeto con la cantidad de respuestas activas (no eliminadas) que un usuario ha realizado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cantidad de respuestas obtenida exitosamente"),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<UserReplyCountDTO> getUserReplyCount(@PathVariable Long userId) {
        UserReplyCountDTO countDTO = replyService.getReplyCountByUser(userId);
        return ResponseEntity.ok(countDTO);
    }

}
