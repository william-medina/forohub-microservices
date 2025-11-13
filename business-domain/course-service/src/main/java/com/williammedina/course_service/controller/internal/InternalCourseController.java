package com.williammedina.course_service.controller.internal;

import com.williammedina.course_service.domain.course.dto.CourseDTO;
import com.williammedina.course_service.domain.course.service.InternalCourseService;
import com.williammedina.course_service.infrastructure.exception.ApiErrorResponse;
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
@RequestMapping(path = "/internal/course", produces = "application/json")
@Tag(name = "Internal Course", description = "Endpoints internos del servicio de coursos disponibles usados para la comunicación entre microservicios.")
@AllArgsConstructor
public class InternalCourseController {

    private final InternalCourseService courseService;

    @Operation(
            summary = "[INTERNAL] Obtener curso por ID",
            description = "Permite obtener un curso específico por su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        CourseDTO course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @Operation(
            summary = "[INTERNAL] Obtener múltiples cursos por IDs",
            description = "Permite obtener varios cursos enviando una lista de IDs separados por coma.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos recuperados exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Lista de IDs inválida", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) })
            }
    )
    @GetMapping("/batch")
    public ResponseEntity<List<CourseDTO>> getCoursesByIds(@RequestParam("ids") List<Long> ids) {
        List<CourseDTO> courses = courseService.getCoursesByIds(ids);
        return ResponseEntity.ok(courses);
    }
}
