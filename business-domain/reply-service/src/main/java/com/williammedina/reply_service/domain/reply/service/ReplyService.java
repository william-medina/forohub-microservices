package com.williammedina.reply_service.domain.reply.service;

import com.williammedina.reply_service.domain.reply.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface ReplyService {

    Mono<ReplyDTO> createReply(Long userId, CreateReplyDTO data);
    Page<ReplyDTO> getAllRepliesByUser(Long userId, Pageable pageable);
    Mono<ReplyDTO> updateReply(Long userId, UpdateReplyDTO data, Long replyId);
    void deleteReply(Long userId, Long replyId);
    ReplyDTO getReplyById(Long replyId);
    ReplyDTO setCorrectReply(Long userId, Long replyId);

}
