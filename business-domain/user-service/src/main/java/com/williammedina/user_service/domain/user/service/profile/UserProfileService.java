package com.williammedina.user_service.domain.user.service.profile;

import com.williammedina.user_service.domain.user.dto.UpdateCurrentUserPasswordDTO;
import com.williammedina.user_service.domain.user.dto.UpdateUsernameDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserProfileService {

    UserDTO updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordDTO data);
    Mono<UserDTO> updateUsername(Long userId, UpdateUsernameDTO data);
    UserDTO getCurrentUser(Long userId);

}
