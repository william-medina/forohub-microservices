package com.williammedina.notification_service.domain.notification.service;

import com.williammedina.notification_service.domain.notification.dto.*;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotificationsByUser(Long userId);
    void deleteNotification(Long userId, Long notifyId);
    NotificationDTO markNotificationAsRead(Long userId, Long notifyId);
    void notifyTopicReply(ReplyDTO reply, TopicSummaryDTO topic, String courseName);
    void notifyTopicSolved(TopicDetailsDTO topic);
    void notifyTopicEdited(TopicDetailsDTO topic);
    void notifyTopicDeleted(TopicDetailsDTO topic);
    void notifyReplySolved(ReplyDTO reply, TopicSummaryDTO topic, String courseName);
    void notifyReplyEdited(ReplyDTO reply, TopicSummaryDTO topic, String courseName);
    void notifyReplyDeleted(ReplyDTO reply, TopicSummaryDTO topic, String courseName);
    void notifyFollowersTopicReply(ReplyDTO reply, TopicSummaryDTO topic, String courseName, Long userId);
    void notifyFollowersTopicSolved(TopicDetailsDTO topic);

}
