package com.williammedina.reply_service.domain.reply.service.internal;

import com.williammedina.reply_service.domain.reply.dto.ReplyCountDTO;
import com.williammedina.reply_service.domain.reply.dto.ReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.TopicSummaryDTO;
import com.williammedina.reply_service.domain.reply.dto.UserReplyCountDTO;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import com.williammedina.reply_service.domain.reply.repository.ReplyRepository;
import com.williammedina.reply_service.domain.reply.service.query.ReplyQueryMapper;
import com.williammedina.reply_service.infrastructure.client.TopicServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalReplyServiceImpl implements InternalReplyService {

    private final ReplyRepository replyRepository;
    private final TopicServiceClient topicServiceClient;
    private final ReplyQueryMapper replyQueryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReplyDTO> getAllRepliesByTopic(Long topicId) {
        log.debug("Fetching replies for topic ID: {}", topicId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(topicId);
        List<ReplyEntity> replies = replyRepository.findByTopicIdAndIsDeletedFalseOrderByCreatedAtDesc(topic.id());

        return replyQueryMapper.mapReplyToDTOs(replies);
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyCountDTO getReplyCountByTopic(Long topicId) {
        log.debug("Fetching reply count for topic ID: {}", topicId);
        Long count = replyRepository.countByTopicIdAndIsDeletedFalse(topicId);
        return new ReplyCountDTO(topicId, count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyCountDTO> getReplyCountsByTopicIds(List<Long> topicIds) {
        log.debug("Fetching reply counts for multiple topics: {}", topicIds);
        return replyRepository.countRepliesByTopicIds(topicIds);
    }

    @Override
    @Transactional(readOnly = true)
    public UserReplyCountDTO getReplyCountByUser(Long userId) {
        long count = replyRepository.countByUserId(userId);
        return new UserReplyCountDTO(count);
    }
}
