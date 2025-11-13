package com.williammedina.user_service.controller.internal;

import com.williammedina.user_service.domain.user.dto.LoginUserDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;
import com.williammedina.user_service.domain.user.service.InternalUserService;
import com.williammedina.user_service.infrastructure.exception.ApiErrorResponse;
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
@RequestMapping(path = "/internal/auth", produces = "application/json")
@Tag(name = "Internal Auth", description = "Endpoints internos del servicio de usuarios usados para la comunicación entre microservicios.")
@AllArgsConstructor
public class InternalUserController {

    private final InternalUserService userService;

    @Operation(
            summary = "[INTERNAL] Obtener usuario por ID",
            description = "Permite obtener un usuario específico por su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "[INTERNAL] Obtener múltiples usuarios por IDs",
            description = "Permite obtener varios usuarios enviando una lista de IDs separados por coma.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuarios recuperados exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Lista de IDs inválida", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<Long> ids) {
        List<UserDTO> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "[INTERNAL] Obtener usuario por ID",
            description = "Permite obtener un usuario específico por su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @PostMapping("/validate-credentials")
    public ResponseEntity<UserDTO> validateCredentials(@RequestBody LoginUserDTO data) {
        UserDTO user = userService.validateCredentials(data);
        return ResponseEntity.ok(user);
    }
}
