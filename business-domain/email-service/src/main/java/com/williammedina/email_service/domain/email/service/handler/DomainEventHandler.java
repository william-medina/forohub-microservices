package com.williammedina.email_service.domain.email.service.handler;

import jakarta.mail.MessagingException;

public interface DomainEventHandler<T> {

    void handle(T event) throws MessagingException;

}
