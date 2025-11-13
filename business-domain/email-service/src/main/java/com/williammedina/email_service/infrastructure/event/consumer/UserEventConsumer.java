package com.williammedina.email_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.email_service.domain.email.dto.UserDTO;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;
import com.williammedina.email_service.domain.usercontact.service.UserContactService;
import com.williammedina.email_service.infrastructure.event.model.user.UserEvent;
import com.williammedina.email_service.infrastructure.event.model.user.UserPayload;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventEntity;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EmailService emailService;
    private final UserContactService userContactService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<UserEvent> userEvents() {
        return event -> {
            log.info("Received event from user-service: {}", event);

            try {

                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

                UserPayload payload = objectMapper.convertValue(event.payload(), UserPayload.class);
                UserDTO user = payload.user();
                String email = payload.email();
                String token = payload.token();

                switch (event.eventType()) {
                    case CREATED_ACCOUNT -> {
                        log.info("Sending email to created account user ID: {}", user.id());
                        UserContactEntity userSaved = userContactService.createUserContact(user, email);
                        emailService.sendConfirmationEmail(new UserDTO(userSaved.getUserId(), userSaved.getUsername(), user.profile()), token);
                    }
                    case REQUEST_CONFIRMATION_CODE -> {
                        log.info("Sending email to request confirmation code user ID: {}", user.id());
                        emailService.sendConfirmationEmail(user, token);
                    }
                    case RESET_PASSWORD -> {
                        log.info("Sending email for reset password user ID: {}", user.id());
                        emailService.sendPasswordResetEmail(user, token);
                    }
                    case UPDATED_USER -> {
                        log.info("Updating user data ID: {}, for user_contact DB", user.id());
                        userContactService.updateUserContact(user);
                    }
                }

                processedEventRepository.save(new ProcessedEventEntity(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing event from user-service: {}", e.getMessage(), e);
            }
        };
    }
}

