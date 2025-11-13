package com.williammedina.topic_read_service.domain.topicread.dto;

import com.williammedina.topic_read_service.domain.topicread.model.Course;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos generales del curso")
public record CourseDTO(

        @Schema(description = "ID del curso", example = "5")
        Long id,

        @Schema(description = "Nombre del curso", example = "Spring Boot Avanzado")
        String name,

        @Schema(description = "Categoría del curso", example = "Java")
        String category
) {

    public static CourseDTO fromModel(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getCategory()
        );
    }

    public static Course fromDto(CourseDTO dto) {
        return new Course(
                dto.id(),
                dto.name(),
                dto.category()
        );
    }
}
