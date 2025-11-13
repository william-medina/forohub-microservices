package com.williammedina.topic_read_service.domain.topicread.dto;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Detalle completo del tópico, incluyendo autor, respuestas y seguidores.")
public record TopicDetailsDTO(

        @Schema(description = "ID del tópico", example = "12")
        Long id,

        @Schema(description = "Título del tópico", example = "Error al ejecutar aplicación Spring Boot")
        String title,

        @Schema(description = "Descripción del tópico", example = "Estoy intentando ejecutar mi aplicación, pero falla al iniciar sin mostrar un mensaje claro.")
        String description,

        @Schema(description = "Nombre del curso al que pertenece el tópico", example = "Desarrollo de Aplicaciones Web con Spring Boot")
        CourseDTO course,

        @Schema(description = "Datos del autor del tópico", example = "William Medina")
        UserDTO author,

        @Schema(description = "Lista de respuestas asociadas al tópico")
        List<ReplyDTO> replies,

        @Schema(description = "Estado actual del tópico", example = "ACTIVE")
        TopicReadDocument.Status status,

        @Schema(description = "Fecha de creación del tópico", example = "2025-05-31T15:45:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización del tópico", example = "2025-07-01T10:15:00")
        LocalDateTime updatedAt,

        @Schema(description = "Lista de seguidores del tópico")
        List<TopicFollowerDTO> followers
) {
    public static TopicDetailsDTO fromDocument(TopicReadDocument topic) {
        return new TopicDetailsDTO(
            topic.getId(),
            topic.getTitle(),
            topic.getDescription(),
            CourseDTO.fromModel(topic.getCourse()),
            UserDTO.fromModel(topic.getAuthor()),
            topic.getReplies().stream().map(ReplyDTO::fromModel).toList(),
            topic.getStatus(),
            topic.getCreatedAt(),
            topic.getUpdatedAt(),
            topic.getFollowers().stream().map(TopicFollowerDTO::fromModel).toList()
        );
    }

    public static TopicReadDocument fromDto(TopicDetailsDTO dto) {
        return TopicReadDocument.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .course(CourseDTO.fromDto(dto.course()))
                .author(UserDTO.fromDto(dto.author()))
                .replies(dto.replies().stream().map(ReplyDTO::fromDto).toList())
                .followers(dto.followers().stream().map(TopicFollowerDTO::fromDto).toList())
                .status(dto.status())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }

}
