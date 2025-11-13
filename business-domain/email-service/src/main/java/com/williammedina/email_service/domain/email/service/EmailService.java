package com.williammedina.email_service.domain.email.service;

import com.williammedina.email_service.domain.email.dto.ReplyDTO;
import com.williammedina.email_service.domain.email.dto.TopicDetailsDTO;
import com.williammedina.email_service.domain.email.dto.TopicSummaryDTO;
import com.williammedina.email_service.domain.email.dto.UserDTO;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendConfirmationEmail(UserDTO user, String token) throws MessagingException;
    void sendPasswordResetEmail(UserDTO user, String token) throws MessagingException;
    void notifyTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException;
    void notifyTopicSolved(TopicDetailsDTO topic) throws MessagingException;
    void notifyTopicEdited(TopicDetailsDTO topic) throws MessagingException;
    void notifyTopicDeleted(TopicDetailsDTO topic) throws MessagingException;
    void notifyReplySolved(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException;
    void notifyReplyEdited(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException;
    void notifyReplyDeleted(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException;
    void notifyFollowersTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException;
    void notifyFollowersTopicSolved(TopicDetailsDTO topic) throws MessagingException ;

}
