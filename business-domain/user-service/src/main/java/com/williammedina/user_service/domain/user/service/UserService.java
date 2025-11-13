package com.williammedina.user_service.domain.user.service;

import com.williammedina.user_service.domain.user.dto.*;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDTO> createAccount(CreateUserDTO data);
    UserDTO confirmAccount(String token);
    UserDTO requestConfirmationCode(EmailUserDTO data);
    UserDTO forgotPassword(EmailUserDTO data);
    UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data);
    UserDTO updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordDTO data);
    Mono<UserDTO> updateUsername(Long userId, UpdateUsernameDTO data);
    UserStatsDTO getUserStats(Long userId);
    UserDTO getCurrentUser(Long userId);

}
