package com.williammedina.notification_service.domain.notification.service;

import com.williammedina.notification_service.domain.notification.repository.NotificationRepository;
import com.williammedina.notification_service.domain.notification.dto.*;
import com.williammedina.notification_service.domain.notification.entity.NotificationEntity;
import com.williammedina.notification_service.infrastructure.client.UserServiceClient;
import com.williammedina.notification_service.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotificationsByUser(Long userId) {
        UserDTO user = userServiceClient.getUserById(userId);
        log.info("Fetching all notifications for user ID: {}", user.id());

        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.id()).stream().map(notify -> NotificationDTO.fromEntity(notify, user)).toList();
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notifyId) {
        NotificationEntity notification = findNotificationById(notifyId);
        UserDTO user = checkModificationPermission(notification, userId);

        notificationRepository.delete(notification);
        log.info("Notification ID: {} deleted by user ID: {}", notifyId, user.id());
    }

    @Override
    @Transactional
    public NotificationDTO markNotificationAsRead(Long userId, Long notifyId) {
        NotificationEntity notification = findNotificationById(notifyId);
        UserDTO user = checkModificationPermission(notification, userId);

        notification.setIsRead(true);
        log.info("Notification ID: {} marked as read by user ID: {}", notifyId, user.id());

        return NotificationDTO.fromEntity(notification, user);
    }

    @Override
    @Transactional
    public void notifyTopicReply(ReplyDTO reply, TopicSummaryDTO topic, String courseName) {

        String title = "Nueva respuesta a tu tópico";
        String message = "Tu tópico ha recibido una nueva respuesta. "
                + reply.author().username() + " respondió al tópico '"
                + topic.title() + "' del curso: " + courseName;

        createNotification(topic.author().id(), topic.id(), null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
        log.info("Reply notification created for topic ID: {} to user ID: {}", topic.id(), topic.author().id());
    }

    @Override
    @Transactional
    public void notifyTopicSolved(TopicDetailsDTO topic) {
        String title = "Tu tópico ha sido marcado como solucionado";
        String message = "Tu tópico '" + topic.title() + "' del curso: " + topic.course().name() + " ha sido marcado como solucionado.";

        createNotification(topic.author().id(), topic.id(), null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.SOLVED);
        log.info("Solved topic notification created for topic ID: {} to user ID: {}", topic.id(), topic.author().id());
    }

    @Override
    @Transactional
    public void notifyTopicEdited(TopicDetailsDTO topic) {
        String title = "Tu tópico ha sido editado";
        String message = "Se ha realizado cambios en tu tópico titulado '" + topic.title() + "' del curso: " + topic.course().name() + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(topic.author().id(), topic.id(), null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.EDITED);
        log.info("Topic edit notification created for topic ID: {} to user ID: {}", topic.id(), topic.author().id());
    }

    @Override
    @Transactional
    public void notifyTopicDeleted(TopicDetailsDTO topic) {
        String title = "Tu tópico ha sido eliminado";
        String message = "Lamentamos informarte que tu tópico titulado '" + topic.title() + "' del curso: " + topic.course().name() + " ha sido eliminado. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(topic.author().id(), null, null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.DELETED);
        log.info("Topic deletion notification created for user ID: {} for deleted topic ID: {}", topic.author().id(), topic.id());
    }

    @Override
    @Transactional
    public void notifyReplySolved(ReplyDTO reply, TopicSummaryDTO topic, String courseName) {

        String title = "Tu respuesta ha sido marcada como solución";
        String message = "Tu respuesta en el tópico '" + topic.title() + "' del curso: " + courseName + " ha sido marcada como solución.";

        createNotification(reply.author().id(), topic.id(), reply.id(), title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.SOLVED);
        log.info("Response marked as solution notification created for user ID: {}", reply.author().id());
    }

    @Override
    @Transactional
    public void notifyReplyEdited(ReplyDTO reply, TopicSummaryDTO topic, String courseName) {

        String title = "Tu respuesta ha sido editada";
        String message = "Se han realizado cambios en tu respuesta del tópico '" + topic.title() + "' del curso: " + courseName + ". Puedes revisar los detalles haciendo clic en el siguiente botón.";

        createNotification(reply.author().id(), reply.topicId(), reply.id(), title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.EDITED);
        log.info("Response edit notification created for user ID: {}", reply.author().id());
    }

    @Override
    @Transactional
    public void notifyReplyDeleted(ReplyDTO reply, TopicSummaryDTO topic, String courseName) {

        String title = "Tu respuesta ha sido eliminada";
        String message = "Lamentamos informarte que tu respuesta del tópico '" + topic.title() + "' del curso: " + courseName + " ha sido eliminada. Si tienes alguna pregunta, por favor contacta a nuestro equipo de soporte.";

        createNotification(reply.author().id(), reply.topicId(), null, title, message, NotificationEntity.Type.REPLY, NotificationEntity.Subtype.DELETED);
        log.info("Response deletion notification created for user ID: {}", reply.author().id());
    }

    @Override
    @Transactional
    public void notifyFollowersTopicReply(ReplyDTO reply, TopicSummaryDTO topic, String courseName, Long userId) {

        String title = "Nueva respuesta en un tópico que sigues";
        String message = "Se ha añadido una nueva respuesta al tópico '" + topic.title() + "' del curso: " + courseName + " que sigues.";

        for (Long followerId : topic.followersIds()) {
            if (!followerId.equals(userId)) {
                createNotification(followerId, topic.id(), null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.REPLY);
            }
        }
    }

    @Override
    @Transactional
    public void notifyFollowersTopicSolved(TopicDetailsDTO topic)  {
        String title = "Un tópico que sigues ha sido marcado como solucionado";
        String message = "El tópico '" + topic.title() + "' del curso: " + topic.course().name() + " que sigues ha sido marcado como solucionado.";

        for (TopicFollowerDTO follower : topic.followers()) {
            createNotification(follower.user().id(), topic.id(), null, title, message, NotificationEntity.Type.TOPIC, NotificationEntity.Subtype.SOLVED);
        }
    }

    private void createNotification(Long userId, Long topicId, Long replyId, String title, String message, NotificationEntity.Type type, NotificationEntity.Subtype subtype) {
        NotificationEntity notification = new NotificationEntity(userId, topicId, replyId, title, message, type, subtype);
        notificationRepository.save(notification);
    }

    private UserDTO checkModificationPermission(NotificationEntity notification, Long userId) {
        UserDTO user = userServiceClient.getUserById(userId);
        if (!notification.getUserId().equals(user.id())) {
            log.warn("User ID: {} attempted to modify a notification that does not belong to them (ID: {})", user.id(), notification.getId());
            throw new AppException("No tienes permiso para eliminar esta notificación", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private NotificationEntity findNotificationById(Long notifyId) {
        return notificationRepository.findById(notifyId)
                .orElseThrow(() -> {
                    log.warn("Notification not found with ID: {}", notifyId);
                    return new AppException("Notificación no encontrada", HttpStatus.NOT_FOUND);
                });
    }
}
