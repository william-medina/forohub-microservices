package com.williammedina.notification_service.controller;

import com.williammedina.notification_service.domain.notification.service.NotificationService;
import com.williammedina.notification_service.domain.notification.dto.NotificationDTO;
import com.williammedina.notification_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/notify", produces = "application/json")
@SecurityRequirement(name = "cookieAuth")
@Tag(name = "Notify", description = "Endpoints para la gestión de notificaciones de los usuarios.")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Obtener todas las notificaciones del usuario",
            description = "Devuelve una lista de todas las notificaciones del usuario autenticado, ordenadas por fecha de creación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de notificaciones devuelta exitosamente"),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsByUser(@RequestHeader("X-User-Id") Long userId) {
        List<NotificationDTO> notifications = notificationService.getAllNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Eliminar una notificación",
            description = "Elimina una notificación específica por su ID, si pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente."),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar esta notificación.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @DeleteMapping("/{notifyId}")
    public ResponseEntity<Void> deleteNotification(@RequestHeader("X-User-Id") Long userId, @PathVariable Long notifyId) {
        notificationService.deleteNotification(userId, notifyId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Marcar una notificación como leída",
            description = "Marca como leída una notificación específica por su ID, si pertenece al usuario autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente."),
                    @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para modificar esta notificación.",  content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada.",  content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible: error de conexión con el microservicio externo", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
            }
    )
    @PatchMapping("/{notifyId}")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long notifyId) {
        NotificationDTO notification = notificationService.markNotificationAsRead(userId, notifyId);
        return ResponseEntity.ok(notification);
    }
}
