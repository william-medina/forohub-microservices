package com.williammedina.reply_service.domain.reply.service.query;

import com.williammedina.reply_service.domain.reply.dto.ReplyDTO;
import com.williammedina.reply_service.domain.reply.dto.UserDTO;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import com.williammedina.reply_service.infrastructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyQueryMapper {

    private final UserServiceClient userServiceClient;

    public List<ReplyDTO> mapReplyToDTOs(List<ReplyEntity> replies) {

        List<Long> userIds = replies.stream()
                .map(ReplyEntity::getUserId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        return replies.stream()
                .map(response -> {
                    UserDTO user = userMap.get(response.getUserId());
                    return ReplyDTO.fromEntity(response, user);
                })
                .toList();
    }
}
