package com.williammedina.topic_read_service.domain.topicread.service;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDetailsDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicFollowDetailsDTO;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TopicReadServiceImpl implements TopicReadService {

    private final TopicReadRepository topicReadRepository;

    @Override
    public Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicReadDocument.Status status) {
        Page<TopicReadDocument> topicsPage;
        log.debug("Fetching topics - page: {}, size: {}, courseId: {}, keyword: {}, status: {}",
                pageable.getPageNumber(), pageable.getPageSize(), courseId, keyword, status);

        if (courseId != null || keyword != null || status != null) {
            topicsPage = topicReadRepository.findByFilters(courseId, keyword, status, pageable);
        } else {
            topicsPage = topicReadRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<TopicDTO> topicDTOS = topicsPage.getContent().stream().map(TopicDTO::fromDocument).toList();
        return new PageImpl<>(topicDTOS, pageable, topicsPage.getTotalElements());
    }

    @Override
    public Page<TopicDTO> getAllTopicsByUser(Long userId, Pageable pageable, String keyword) {
        Page<TopicReadDocument> topicsPage;
        log.debug("Fetching topics for user ID: {} - keyword: {}", userId, keyword);

        if (keyword != null ) {
            topicsPage = topicReadRepository.findByUserIdWithKeyword(userId, keyword, pageable);
        } else {
            topicsPage = topicReadRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageable);
        }

        List<TopicDTO> topicDTOS = topicsPage.getContent().stream().map(TopicDTO::fromDocument).toList();
        return new PageImpl<>(topicDTOS, pageable, topicsPage.getTotalElements());
    }

    @Override
    public TopicDetailsDTO getTopicById(Long topicId) {
        log.info("Fetching topic details with ID: {}", topicId);
        TopicReadDocument topic = findTopicById(topicId);
        return TopicDetailsDTO.fromDocument(topic);
    }

    @Override
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Long userId, Pageable pageable, String keyword) {
        Page<TopicReadDocument> topicsPage;
        log.debug("Fetching followed topics for user ID: {}", userId);

        if (keyword != null ) {
            topicsPage = topicReadRepository.findByFollowerUserIdAndKeyword(userId, keyword, pageable);
        } else {
            topicsPage = topicReadRepository.findByFollowerUserId(userId, pageable);
        }

        List<TopicFollowDetailsDTO> topicFollowerDTOs = topicsPage.getContent().stream().map(topic -> TopicFollowDetailsDTO.fromTopicReadDocument(userId, topic)).toList();
        return new PageImpl<>(topicFollowerDTOs, pageable, topicsPage.getTotalElements());
    }

    public TopicReadDocument findTopicById(Long topicId) {
        return topicReadRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("Topic not found with ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }
}
