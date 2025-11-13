package com.williammedina.user_service.domain.user.service;

import com.williammedina.user_service.domain.user.dto.CreateUserDTO;
import com.williammedina.user_service.domain.user.dto.LoginUserDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;

import java.util.List;

public interface InternalUserService {

    UserDTO getUserById(Long userId);
    List<UserDTO> getUsersByIds(List<Long> ids);
    UserDTO validateCredentials(LoginUserDTO data);
}
