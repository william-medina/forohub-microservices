package com.williammedina.reply_service.infrastructure.client;

import com.williammedina.reply_service.domain.reply.dto.InputTopicStatusDTO;
import com.williammedina.reply_service.domain.reply.dto.TopicDetailsDTO;
import com.williammedina.reply_service.domain.reply.dto.TopicSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "topic-service", path = "/internal/topic")
public interface TopicServiceClient {

    @GetMapping("/summary/{topicId}")
    TopicSummaryDTO getTopicSummaryById(@PathVariable("topicId") Long id);

    @PostMapping("/{topicId}/status")
    void changeTopicStatus(@PathVariable("topicId") Long id, @RequestBody InputTopicStatusDTO data);

}
