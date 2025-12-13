package com.williammedina.topic_read_service.domain.topicread.service.handler;

public interface DomainEventHandler<T> {

    void handle(T event);

}
