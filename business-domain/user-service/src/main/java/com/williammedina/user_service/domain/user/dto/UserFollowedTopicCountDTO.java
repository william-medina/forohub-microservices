package com.williammedina.user_service.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que contiene el número de tópicos que un usuario está siguiendo")
public record UserFollowedTopicCountDTO(

        @Schema(description = "Número de tópicos que el usuario sigue", example = "8")
        Long count

) {}
