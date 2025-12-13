package com.williammedina.user_service.domain.user.service.internal;

import com.williammedina.user_service.domain.user.dto.LoginUserDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.domain.user.service.finder.UserFinder;
import com.williammedina.user_service.domain.user.service.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalUserServiceImpl implements InternalUserService {

    private final UserRepository userRepository;
    private final UserFinder userFinder;
    private final UserValidator validator;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        UserEntity user = userFinder.findUserById(userId);
        log.debug("Fetching user data by ID: {}", user.getId());
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByIds(List<Long> ids) {
        List<UserEntity> users = userRepository.findAllById(ids);
        log.debug("Fetching user data for IDs: {}", ids);
        return users.stream().map(UserDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO validateCredentials(LoginUserDTO request) {
        UserEntity user = userFinder.findUserByEmailOrUsername(request.username(), request.username());
        validator.validateCurrentPassword(user, request.password());
        return UserDTO.fromEntity(user);
    }
}
