package com.williammedina.content_analysis_service.infrastructure.contentvalidation;

import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationResponse;
import com.williammedina.content_analysis_service.domain.contentvalidation.service.ContentValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@ConditionalOnProperty(value = "ai.enabled", havingValue = "true")
public class AIContentValidationService implements ContentValidationService {

    private final ChatClient chatClient;

    public AIContentValidationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Mono<ContentValidationResponse> validateContent(String content) {
        log.debug("Validating content with AI: {}", content);

        return Mono.fromCallable(() -> {
            String systemMessage = """
                Eres una IA de moderación de contenido en un foro educativo que trata sobre cursos. Tu tarea es evaluar el texto proporcionado y determinar si contiene contenido inapropiado o irrelevante para los temas de los cursos. Debes identificar cualquier forma de contenido que pueda ser considerado:
            
                - Ofensivo
                - Racista
                - Xenófobo
                - Inapropiado
                - Spam (como promociones o enlaces irrelevantes)
                - Desinformación
                - Amenazas
                - Contenido explícito
                - Repetitivo
            
                Si el contenido es inapropiado, responde con una breve descripción de los problemas encontrados, siempre iniciando con la palabra "contiene" en minúscula. No utilices caracteres especiales ni mayúsculas al principio. La respuesta debe finalizar con un punto.
            
                Ejemplos de respuestas inapropiadas:
                - "contiene palabras sin sentido"
                - "contiene palabras repetitivas"
                - "contiene caracteres ofensivos"
                - "contiene información errónea"
            
                Si el contenido es adecuado (sin problemas), responde con:
                - "approved" (sin puntos ni caracteres especiales)
            
                Ejemplo de respuesta válida:
                - "approved"
            
                Texto:
            """;
            String aiResponse = chatClient.prompt().system(systemMessage).user(content).call().content();
            log.info("Content validation result: {}", aiResponse.trim());

            return new ContentValidationResponse(aiResponse.trim());
        }).subscribeOn(Schedulers.boundedElastic()); // Ejecuta bloqueante de manera no bloqueante
    }

    @Override
    public Mono<ContentValidationResponse> validateUsername(String username) {
        log.debug("Validating username with AI: {}", username);

        return Mono.fromCallable(() -> {

            String systemMessage = """
            Eres una IA de moderación de contenido en una plataforma educativa. Tu tarea es evaluar el siguiente nombre de usuario y determinar si es adecuado para un foro o comunidad en línea relacionada con cursos y educación. Debes verificar si el nombre contiene algún tipo de contenido inapropiado o no deseado.
        
            Debes buscar contenido inapropiado en nombres de usuario, incluyendo casos donde las palabras estén separadas o concatenadas.
        
            Tipos de problemas que debes buscar en el nombre de usuario:
                - Ofensivo
                - Racista
                - Xenófobo
                - Spam
                - Inapropiado
                - Aleatorio o sin sentido (como cadenas de caracteres sin un propósito claro)
                - Burla o comentario despectivo
                - Combinaciones de palabras que puedan interpretarse como irrespetuosas o inadecuadas (separadas o juntas).
                - Cualquier otra forma de contenido nocivo
        
        
            Si el nombre de usuario es inapropiado, responde con una breve descripción de los problemas encontrados, siempre iniciando con la palabra "contiene" en minúscula. No utilices caracteres especiales ni mayúsculas al principio. La respuesta debe finalizar con un punto.
        
            Ejemplos de respuestas inapropiadas:
            - "contiene palabras ofensivas"
            - "contiene nombre de usuario aleatorio"
            - "contiene caracteres inapropiados"
        
            Si el nombre de usuario es adecuado (sin problemas), responde con:
            - "approved" (sin puntos ni caracteres especiales)
        
            Ejemplo de respuesta válida:
            - "approved"
        
            Nombre de usuario:
        """;

            String aiResponse = chatClient.prompt().system(systemMessage).user(username).call().content();
            log.info("Username '{}' validation result: {}", username, aiResponse.trim());

            return new ContentValidationResponse(aiResponse.trim());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
