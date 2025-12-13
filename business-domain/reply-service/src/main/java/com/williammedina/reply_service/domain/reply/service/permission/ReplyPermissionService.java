package com.williammedina.reply_service.domain.reply.service.permission;

import com.williammedina.reply_service.domain.reply.dto.UserDTO;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;

public interface ReplyPermissionService {

    UserDTO checkCanModify(Long userId, ReplyEntity reply);
    void checkElevatedPermissionsForSolution(UserDTO user, Long replyId);
    void checkCannotDeleteSolution(ReplyEntity reply);

}
