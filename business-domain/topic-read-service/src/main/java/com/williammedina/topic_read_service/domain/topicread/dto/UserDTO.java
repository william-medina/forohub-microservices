package com.williammedina.topic_read_service.domain.topicread.dto;

import com.williammedina.topic_read_service.domain.topicread.model.Author;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información pública del usuario")
public record UserDTO(

        @Schema(description = "ID del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre de usuario", example = "admin123")
        String username,

        @Schema(description = "Rol del usuario", example = "ADMIN")
        String profile
) {

    public static UserDTO fromModel(Author user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getProfile()
        );
    }

    public static Author fromDto(UserDTO dto) {
        return new Author(
                dto.id(),
                dto.username(),
                dto.profile()
        );
    }
}
