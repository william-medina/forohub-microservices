package com.williammedina.user_service.domain.user.service.profile;

import com.williammedina.user_service.domain.user.dto.UpdateCurrentUserPasswordDTO;
import com.williammedina.user_service.domain.user.dto.UpdateUsernameDTO;
import com.williammedina.user_service.domain.user.dto.UserDTO;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.domain.user.service.finder.UserFinder;
import com.williammedina.user_service.domain.user.service.validator.UserValidator;
import com.williammedina.user_service.infrastructure.client.ContentValidationClient;
import com.williammedina.user_service.infrastructure.event.model.UserEventType;
import com.williammedina.user_service.infrastructure.event.model.UserPayload;
import com.williammedina.user_service.infrastructure.event.publisher.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserFinder userFinder;
    private final UserValidator validator;
    private final ContentValidationClient contentValidationClient;
    private final UserEventPublisher userEventPublisher;


    @Override
    @Transactional
    public UserDTO updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordDTO request) {
        UserEntity currentUser = userFinder.findUserById(userId);
        validator.validatePasswordsMatch(request.password(), request.password_confirmation());
        validator.validateCurrentPassword(currentUser, request.current_password());

        currentUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(currentUser);
        log.info("Password successfully changed for user ID: {}", currentUser.getId());

        return UserDTO.fromEntity(currentUser);
    }

    @Override
    @Transactional
    public Mono<UserDTO> updateUsername(Long userId, UpdateUsernameDTO request) {
        UserEntity currentUser = userFinder.findUserById(userId);

        validator.ensureNewUsername(currentUser, request.username());
        validator.existsByUsername(request.username());

        return contentValidationClient.validateUsername(request.username())
                .publishOn(Schedulers.boundedElastic())
                .map(contentResult -> {

                    validator.validateUsernameContent(contentResult); // Validate the new username using AI
                    currentUser.setUsername(request.username());
                    UserEntity userUpdated = userRepository.save(currentUser);

                    log.info("Username updated to '{}' for user ID: {}", request.username(), currentUser.getId());
                    UserDTO userDTO = UserDTO.fromEntity(userUpdated);
                    UserPayload userPayload = new UserPayload(userDTO, null, null);
                    userEventPublisher.publishEvent(UserEventType.UPDATED_USER, userPayload);

                    return userDTO;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(Long userId) {
        UserEntity currentUser = userFinder.findUserById(userId);
        log.debug("Fetching authenticated user data. User ID: {}", currentUser.getId());
        return UserDTO.fromEntity(currentUser);
    }
}
