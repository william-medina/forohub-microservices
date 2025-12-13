package com.williammedina.reply_service.controller;

import com.williammedina.reply_service.domain.reply.service.ReplyService;
import com.williammedina.reply_service.domain.reply.dto.CreateReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.ReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.UpdateReplyDTO;
import com.williammedina.reply_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/reply", produces = "application/json")
@Tag(name = "Reply", description = "Endpoints para la gestión de respuestas en los tópicos del foro.")
@AllArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @Operation(
            summary = "Crear una nueva respuesta",
            description = "Permite a un usuario crear una respuesta para un tópico específico.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Respuesta creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "No se pueden agregar respuestas a un tópico cerrado o contenido inapropiado detectado por la IA.", content = { @Content( schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Mono<ReplyDTO>> createReply(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid CreateReplyDTO replyRequest) {
        Mono<ReplyDTO> reply = replyService.createReply(userId, replyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @Operation(
            summary = "Obtener todas las respuestas del usuario autenticado",
            description = "Recupera todas las respuestas creadas por el usuario actualmente autenticado con paginación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuestas recuperadas exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @GetMapping("/user/replies")
    public ResponseEntity<Page<ReplyDTO>> getAllRepliesByUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReplyDTO> replies = replyService.getAllRepliesByUser(userId, pageable);
        return ResponseEntity.ok(replies);
    }

    @Operation(
            summary = "Obtener una respuesta por ID",
            description = "Recupera una respuesta específica utilizando su identificador único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta recuperada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @GetMapping("/{replyId}")
    public ResponseEntity<ReplyDTO> getReplyById(@PathVariable Long replyId) {
        ReplyDTO reply = replyService.getReplyById(replyId);
        return ResponseEntity.ok(reply);
    }

    @Operation(
            summary = "Actualizar una respuesta",
            description = "Permite a un usuario actualizar el contenido de una respuesta específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta actualizada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta respuesta o contenido inapropiado detectado por la IA.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PutMapping("/{replyId}")
    public ResponseEntity<Mono<ReplyDTO>> updateReply(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid UpdateReplyDTO replyRequest,
            @PathVariable Long replyId
    ) {
        Mono<ReplyDTO> reply = replyService.updateReply(userId, replyRequest, replyId);
        return ResponseEntity.ok(reply);
    }


    @Operation(
            summary = "Alternar el estado de una respuesta como solución",
            description = "Permite marcar una respuesta como la solución correcta de un tópico o quitarla como solución si ya estaba marcada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado de solución actualizado exitosamente"),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta respuesta.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PatchMapping("/{replyId}")
    public ResponseEntity<ReplyDTO> setCorrectReply(@RequestHeader("X-User-Id") Long userId, @PathVariable Long replyId){
        ReplyDTO reply = replyService.setCorrectReply(userId, replyId);
        return ResponseEntity.ok(reply);
    }

    @Operation(
            summary = "Eliminar una respuesta",
            description = "Permite a un usuario eliminar una respuesta específica.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Respuesta eliminada exitosamente"),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar esta respuesta.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Respuesta no encontrada", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "409", description = "No se puede eliminar una respuesta marcada como solución", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@RequestHeader("X-User-Id") Long userId, @PathVariable Long replyId) {
        replyService.deleteReply(userId, replyId);
        return ResponseEntity.noContent().build();
    }


}
