package com.williammedina.reply_service.domain.reply.service.permission;

import com.williammedina.reply_service.domain.reply.dto.UserDTO;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import com.williammedina.reply_service.infrastructure.client.UserServiceClient;
import com.williammedina.reply_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyPermissionServiceImpl implements ReplyPermissionService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDTO checkCanModify(Long userId, ReplyEntity reply) {
        UserDTO user = userServiceClient.getUserById(userId);
        if (!reply.getUserId().equals(user.id()) && !user.hasElevatedPermissions()) {
            log.warn("User ID: {} without permission attempted to modify reply ID: {}", user.id(), reply.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    @Override
    public void checkElevatedPermissionsForSolution(UserDTO user, Long replyId) {
        if (!user .hasElevatedPermissions()) {
            log.warn("User ID: {} without permissions attempted to change reply ID: {}", user.id(), replyId);
            throw new AppException("No tienes permiso para modificar el estado de la respuesta", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void checkCannotDeleteSolution(ReplyEntity reply) {
        if (reply.getSolution()) {
            log.warn("Attempt to delete reply ID: {} marked as solution", reply.getId());
            throw new AppException("No puedes eliminar una respuesta marcada como solución", HttpStatus.CONFLICT);
        }
    }
}
