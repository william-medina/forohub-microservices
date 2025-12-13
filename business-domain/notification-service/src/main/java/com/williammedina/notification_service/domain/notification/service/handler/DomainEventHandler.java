package com.williammedina.notification_service.domain.notification.service.handler;

public interface DomainEventHandler<T> {

    void handle(T event);

}
