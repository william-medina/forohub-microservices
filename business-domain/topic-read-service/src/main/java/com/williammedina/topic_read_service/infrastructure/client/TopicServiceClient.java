package com.williammedina.topic_read_service.infrastructure.client;

import com.williammedina.topic_read_service.domain.topicread.dto.TopicDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@FeignClient(name = "topic-service", path = "/internal/topic")
public interface TopicServiceClient {

    @GetMapping("/export")
    List<TopicDetailsDTO> exportAllTopics();

}
