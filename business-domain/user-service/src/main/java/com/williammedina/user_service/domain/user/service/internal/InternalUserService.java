package com.williammedina.user_service.domain.user.service.internal;

import com.williammedina.user_service.domain.user.dto.LoginUserDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;

import java.util.List;

public interface InternalUserService {

    UserDTO getUserById(Long userId);
    List<UserDTO> getUsersByIds(List<Long> ids);
    UserDTO validateCredentials(LoginUserDTO request);
}
