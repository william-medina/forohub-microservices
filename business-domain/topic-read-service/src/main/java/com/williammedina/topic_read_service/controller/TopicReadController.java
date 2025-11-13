package com.williammedina.topic_read_service.controller;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDetailsDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicFollowDetailsDTO;
import com.williammedina.topic_read_service.domain.topicread.service.TopicReadService;
import com.williammedina.topic_read_service.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicReadController {

    private final TopicReadService topicReadService;

    @Operation(
            summary = "Obtener todos los tópicos",
            description = "Permite obtener una lista de todos los tópicos, con paginación y filtrado opcional por curso, palabra clave y estado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópicos recuperados exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Curso o usuario no encontrado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
            }
    )
    @GetMapping
    public ResponseEntity<Page<TopicDTO>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TopicReadDocument.Status status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicDTO> topics = topicReadService.getAllTopics(pageable, courseId, keyword, status);
        return ResponseEntity.ok(topics);
    }

    @Operation(
            summary = "Obtener los tópicos del usuario",
            description = "Permite obtener los tópicos creados por el usuario, con paginación y filtrado opcional por palabra clave.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópicos recuperados exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Curso o usuario no encontrado.", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
            }
    )
    @GetMapping("/user/topics")
    public ResponseEntity<Page<TopicDTO>> getAllTopicsByUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicDTO> topics = topicReadService.getAllTopicsByUser(userId, pageable, keyword);
        return ResponseEntity.ok(topics);
    }

    @Operation(
            summary = "Obtener un tópico por ID",
            description = "Permite obtener un tópico específico por su ID, incluyendo todas sus respuestas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tópico recuperado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tópico no encontrado", content = { @Content(schema = @Schema(implementation = ApiErrorResponse.class)) }),
            }
    )
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDetailsDTO> getTopicById(@PathVariable Long topicId) {
        TopicDetailsDTO topic = topicReadService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/user/followed-topics")
    public ResponseEntity<Page<TopicFollowDetailsDTO>> getFollowedTopicsByUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TopicFollowDetailsDTO> topics = topicReadService.getFollowedTopicsByUser(userId, pageable, keyword);
        return ResponseEntity.ok(topics);
    }

}
