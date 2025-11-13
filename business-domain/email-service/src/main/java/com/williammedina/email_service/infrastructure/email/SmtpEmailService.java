package com.williammedina.email_service.infrastructure.email;

import com.williammedina.email_service.domain.email.dto.*;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;
import com.williammedina.email_service.domain.usercontact.repository.UserContactRepository;
import com.williammedina.email_service.domain.usercontact.service.UserContactQueryService;
import com.williammedina.email_service.infrastructure.exception.AppException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "email.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final Environment environment;
    private final UserContactRepository userContactRepository;
    private final UserContactQueryService userContactService;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    @Override
    public void sendConfirmationEmail(UserDTO user, String token) throws MessagingException {
        UserContactEntity userContact = userContactService.findUserById(user.id());

        String subject = "Confirmación de cuenta";
        String url = frontendUrl + "/confirm-account/" + token;
        String title = "¡Bienvenido a Foro Hub!";
        String message = "Hola <b style='color: #03dac5;'>" + userContact.getUsername() + "</b>, para completar tu registro, haz clic en el siguiente enlace para confirmar tu cuenta:";
        String buttonLabel = "Confirmar Cuenta";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing confirmation email for user ID: {}", userContact.getUserId());
        sendEmail(userContact.getEmail(), subject, title, message, buttonLabel, url, footer);
        log.info("Confirmation email sent to: {}", userContact.getEmail());
    }

    @Override
    public void sendPasswordResetEmail(UserDTO user, String token) throws MessagingException {
        UserContactEntity userContact = userContactService.findUserById(user.id());

        String subject = "Restablecimiento de password";
        String url = frontendUrl + "/reset-password/" + token;
        String title = "Restablecimiento de Password";
        String message = "Hola <b style='color: #03dac5;'>" + userContact.getUsername() + "</b>, has solicitado restablecer tu password. Haz clic en el siguiente enlace para crear un nuevo password:";
        String buttonLabel = "Restablecer Password";
        String footer = "Si no solicitaste este email, puedes ignorarlo.";

        log.info("Preparing password reset email for user ID: {}", userContact.getUserId());
        sendEmail(userContact.getEmail(), subject, title, message, buttonLabel, url, footer);
        log.info("Password reset email sent to: {}", userContact.getEmail());
    }

    @Override
    public void notifyTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException {
        UserContactEntity replyingUser = userContactService.findUserById(userId);
        UserContactEntity topicAuthor = userContactService.findUserById(topic.author().id());

        String subject = "Nueva respuesta a tu tópico";
        String actionMessage = "<b style='color: #03dac5;'>" + replyingUser.getUsername() + "</b> respondió a tu tópico ";
        String topicDetails = "<b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + courseName + "</b>.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        String htmlContent = "<p>" + actionMessage + " " + topicDetails + "</p>";

        sendEmail(topicAuthor.getEmail(), subject, subject, htmlContent, "Ver Tópico", url, footer);
    }

    @Override
    public void notifyTopicSolved(TopicDetailsDTO topic) throws MessagingException {
        UserContactEntity topicAuthor = userContactService.findUserById(topic.author().id());

        String subject = "Tu tópico ha sido marcado como solucionado";
        String actionMessage = "Tu tópico <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + topic.course().name() + "</b> ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topicAuthor.getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    public void notifyTopicEdited(TopicDetailsDTO topic) throws MessagingException {
        UserContactEntity topicAuthor  = userContactService.findUserById(topic.author().id());

        String subject = "Tu tópico ha sido editado";
        String actionMessage = "Se ha realizado cambios en tu tópico titulado <b style='color: #03dac5;'>\"" + topic.title() + "</b> del curso: <b>" + topic.course().name() + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topicAuthor.getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
    }

    @Override
    public void notifyTopicDeleted(TopicDetailsDTO topic) throws MessagingException {
        UserContactEntity topicAuthor  = userContactService.findUserById(topic.author().id());

        String subject = "Tu tópico ha sido eliminado";
        String actionMessage = "Lamentamos informarte que tu tópico titulado <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + topic.course().name() + "</b> ha sido eliminado. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(topicAuthor.getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Override
    public void notifyReplySolved(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        UserContactEntity replyAuthor  = userContactService.findUserById(reply.author().id());

        String subject = "Tu respuesta ha sido marcada como solución";
        String actionMessage = "Tu respuesta en el tópico <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + courseName + "</b> ha sido marcada como solución.";

        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(replyAuthor.getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    public void notifyReplyEdited(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        UserContactEntity replyAuthor  = userContactService.findUserById(reply.author().id());

        String subject = "Tu respuesta ha sido editada";
        String actionMessage = "Se ha realizado cambios en tu respuesta del tópico <b style='color: #03dac5;'>\"" + topic.title() + "</b> del curso: <b>" + courseName + "</b>. Puedes revisar los detalles haciendo clic en el siguiente botón.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(replyAuthor.getEmail(), subject, subject, actionMessage, "Ver Respuesta", url, footer);
    }

    @Override
    public void notifyReplyDeleted(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        UserContactEntity replyAuthor  = userContactService.findUserById(reply.author().id());

        String subject = "Tu respuesta ha sido eliminada";
        String actionMessage = "Lamentamos informarte que tu respuesta del tópico <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + courseName + "</b> ha sido eliminada. Si tienes alguna pregunta o inquietud, por favor contacta a nuestro equipo de soporte para más detalles.";
        String footer = "Gracias por ser parte de ForoHub.";

        sendEmail(replyAuthor.getEmail(), subject, subject, actionMessage, null, null, footer);
    }

    @Override
    @Async
    public void notifyFollowersTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException {

        String subject = "Nueva respuesta en un tópico que sigues";
        String actionMessage = "Se ha añadido una nueva respuesta al tópico <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + courseName + "</b> que sigues.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        List<UserContactEntity> followers = userContactRepository.findAllById(topic.followersIds());

        // Batching to avoid overloading the mailSender
        int batchSize = 50;
        for (int i = 0; i < followers.size(); i += batchSize) {
            List<UserContactEntity> batch = followers.subList(i, Math.min(i + batchSize, followers.size()));

            for (UserContactEntity follower : batch) {
                if (!follower.getUserId().equals(userId)) {
                    try {
                        sendEmail(follower.getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
                    } catch (MailException | MessagingException e) {
                        log.error("notifyFollowersTopicReply - Failed to send email to {}: {}", follower.getEmail(), e.getMessage());
                    }
                }
            }

            // Small pause between batches to avoid overloading the mail server
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("notifyFollowersTopicReply - Thread interrupted during sleep", e);
            }
        }
    }

    @Override
    @Async
    public void notifyFollowersTopicSolved(TopicDetailsDTO topic) throws MessagingException {
        String subject = "Un tópico que sigues ha sido marcado como solucionado";
        String actionMessage = "El tópico <b style='color: #03dac5;'>" + topic.title() + "</b> del curso: <b>" + topic.course().name() + "</b> que sigues ha sido marcado como solucionado.";
        String url = frontendUrl + "/topic/" + topic.id();
        String footer = "Gracias por ser parte de ForoHub.";

        Set<Long> followerIds = topic.followers().stream()
                .map(f -> f.user().id())
                .collect(Collectors.toSet());

        List<UserContactEntity> followers = userContactRepository.findAllById(followerIds);

        // Batching to avoid overloading the mailSender
        int batchSize = 50;
        for (int i = 0; i < followers.size(); i += batchSize) {
            List<UserContactEntity> batch = followers.subList(i, Math.min(i + batchSize, followers.size()));

            for (UserContactEntity follower : batch) {
                try {
                    sendEmail(follower.getEmail(), subject, subject, actionMessage, "Ver Tópico", url, footer);
                } catch (MailException | MessagingException e) {
                    log.error("notifyFollowersTopicSolved - Failed to send email to {}: {}", follower.getEmail(), e.getMessage());
                }
            }

            // Small pause between batches to avoid overloading the mail server
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("notifyFollowersTopicSolved - Thread interrupted during sleep", e);
            }
        }
    }

    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) throws MessagingException {

        String htmlContent = buildEmailContent(title, message, buttonLabel, url, footer);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String emailFrom = environment.getProperty("EMAIL_FROM");

        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("Failed to send email to: {}. Reason: {}", to, e.getMessage());
            throw new AppException("Error al enviar el email. Intenta más tarde.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String buildEmailContent(String title, String message, String buttonLabel, String url, String footer) {
        String template = loadTemplate("email_template.html")
                .replace("{TITLE}", title)
                .replace("{MESSAGE}", message)
                .replace("{FOOTER}", footer);

        // Check if buttonLabel and url are null
        String buttonSection = (buttonLabel != null && url != null)
                ? "<a href=\"" + url + "\" style=\"display: inline-block; background-color: #03dac5; color: #121212; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; max-width: 100%; text-align: center;\">"
                + buttonLabel + "</a>"
                : "";

        return template.replace("{BUTTON_SECTION}", buttonSection);
    }

    // Loads a template file from the file system
    private String loadTemplate(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + fileName)) {
            if (inputStream == null) {
                log.error("Email template not found: {}", fileName);
                throw new RuntimeException("No se pudo encontrar la plantilla de email: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            log.error("Failed to load email template: {}. Details: {}", fileName, e.getMessage());
            throw new RuntimeException("Error al cargar la plantilla de email: " + fileName, e);
        }
    }

}
