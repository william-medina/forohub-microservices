package com.williammedina.reply_service.domain.reply.service.finder;

import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;

public interface ReplyFinder {

    ReplyEntity findReplyById(Long replyId);

}
