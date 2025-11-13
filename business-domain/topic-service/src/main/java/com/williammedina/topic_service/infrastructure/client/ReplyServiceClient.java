package com.williammedina.topic_service.infrastructure.client;

import com.williammedina.topic_service.domain.topic.dto.ReplyCountDTO;
import com.williammedina.topic_service.domain.topic.dto.ReplyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "reply-service", path = "/internal/reply")
public interface ReplyServiceClient {

    @GetMapping("/topic/{topicId}")
    List<ReplyDTO> getAllRepliesByTopic(@PathVariable("topicId") Long id);

    @GetMapping("/topic/{topicId}/count")
    ReplyCountDTO getReplyCountByTopic(@PathVariable("topicId") Long id);

    @GetMapping("/count/batch")
    List<ReplyCountDTO> getReplyCountsByTopicIds(@RequestParam("ids") List<Long> topicIds);

}
