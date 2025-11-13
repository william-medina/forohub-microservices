package com.williammedina.reply_service.domain.reply.service;

import com.williammedina.reply_service.domain.reply.dto.ReplyCountDTO;
import com.williammedina.reply_service.domain.reply.dto.ReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.UserReplyCountDTO;

import java.util.List;

public interface InternalReplyService {

    List<ReplyDTO> getAllRepliesByTopic(Long topicId);
    ReplyCountDTO getReplyCountByTopic(Long topicId);
    List<ReplyCountDTO> getReplyCountsByTopicIds(List<Long> topicIds);
    UserReplyCountDTO getReplyCountByUser(Long userId);

}
