package com.williammedina.user_service.domain.user.service;

import com.williammedina.user_service.domain.user.dto.*;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.infrastructure.client.ContentValidationClient;
import com.williammedina.user_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.user_service.infrastructure.client.TopicServiceClient;
import com.williammedina.user_service.infrastructure.event.model.UserEventType;
import com.williammedina.user_service.infrastructure.event.model.UserPayload;
import com.williammedina.user_service.infrastructure.event.publisher.UserEventPublisher;
import com.williammedina.user_service.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, InternalUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContentValidationClient contentValidationClient;
    private final UserEventPublisher userEventPublisher;
    private final TopicServiceClient topicServiceClient;
    private final ReplyServiceClient replyServiceClient;

    @Override
    @Transactional
    public Mono<UserDTO> createAccount(CreateUserDTO data) {
        log.info("Creating account for: {}", data.username());

        validatePasswordsMatch(data.password(), data.password_confirmation());
        existsByUsername(data.username());
        existsByEmail(data.email());

        return contentValidationClient.validateUsername(data.username())
            .publishOn(Schedulers.boundedElastic())
            .map(contentResult -> {

                validateUsernameContent(contentResult); // Validate the username using AI

                UserEntity user = new UserEntity(data.username(), data.email().trim().toLowerCase(), passwordEncoder.encode(data.password()));
                UserEntity userCreated = userRepository.save(user);

                log.info("User created successfully - ID: {}", userCreated.getId());
                UserDTO userDTO = UserDTO.fromEntity(userCreated);
                UserPayload userPayload = new UserPayload(userDTO, user.getEmail(), user.getToken());
                userEventPublisher.publishEvent(UserEventType.CREATED_ACCOUNT, userPayload);

                return userDTO;
            });
    }

    @Override
    @Transactional
    public UserDTO confirmAccount(String token) {
        log.info("Confirming account with token");

        UserEntity user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountConfirmed(user);

        user.setAccountConfirmed(true);
        user.clearTokenData();
        userRepository.save(user);
        log.info("Account confirmed for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO requestConfirmationCode(EmailUserDTO data) {
        log.info("Requesting confirmation code for email: {}", data.email());

        UserEntity user = findUserByEmail(data.email());
        checkIfAccountConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            log.warn("Confirmation request too soon for user ID: {}", user.getId());
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de confirmación.", HttpStatus.BAD_REQUEST);
        }

        user.generateConfirmationToken();
        log.info("Confirmation code generated and sent for user ID: {}", user.getId());

        UserDTO userDTO = UserDTO.fromEntity(user);
        UserPayload userPayload = new UserPayload(userDTO, null, user.getToken());
        userEventPublisher.publishEvent(UserEventType.REQUEST_CONFIRMATION_CODE, userPayload);

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO forgotPassword(EmailUserDTO data) {
        log.info("Password reset requested for: {}", data.email());

        UserEntity user = findUserByEmail(data.email());
        checkIfAccountNotConfirmed(user);

        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            log.warn("Password reset request too soon for user ID: {}", user.getId());
            throw new AppException("Debe esperar 2 minutos para solicitar otro código de restablecimiento de password.", HttpStatus.BAD_REQUEST);
        }

        user.generateConfirmationToken();
        log.info("Password reset code generated and sent for user ID: {}", user.getId());

        UserDTO userDTO = UserDTO.fromEntity(user);
        UserPayload userPayload = new UserPayload(userDTO, null, user.getToken());
        userEventPublisher.publishEvent(UserEventType.RESET_PASSWORD, userPayload);

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data) {
        log.info("Updating password using token");

        validatePasswordsMatch(data.password(), data.password_confirmation());

        UserEntity user = findUserByToken(token);
        validateTokenExpiration(user);
        checkIfAccountNotConfirmed(user);

        user.setPassword(passwordEncoder.encode(data.password()));
        user.clearTokenData();
        userRepository.save(user);
        log.info("Password updated for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO updateCurrentUserPassword(Long userId, UpdateCurrentUserPasswordDTO data) {
        UserEntity user = findUserById(userId);
        validatePasswordsMatch(data.password(), data.password_confirmation());

        if (!passwordEncoder.matches(data.current_password(), user.getPassword())) {
            log.warn("Incorrect current password for user ID: {}", user.getId());
            throw new AppException("El password actual es incorrecto.", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(data.password()));
        userRepository.save(user);
        log.info("Password successfully changed for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public Mono<UserDTO> updateUsername(Long userId, UpdateUsernameDTO data) {
        UserEntity user = findUserById(userId);

        if (user.getUsername().equals(data.username())) {
            log.warn("Attempt to update to same username - user ID: {}", user.getId());
            throw new AppException("Debes ingresa un nuevo nombre.", HttpStatus.BAD_REQUEST);
        }

        existsByUsername(data.username());

        return contentValidationClient.validateUsername(data.username())
            .publishOn(Schedulers.boundedElastic())
            .map(contentResult -> {

                validateUsernameContent(contentResult); // Validate the new username using AI
                user.setUsername(data.username());
                UserEntity userUpdated = userRepository.save(user);

                log.info("Username updated to '{}' for user ID: {}", data.username(), user.getId());
                UserDTO userDTO = UserDTO.fromEntity(userUpdated);
                UserPayload userPayload = new UserPayload(userDTO, null, null);
                userEventPublisher.publishEvent(UserEventType.UPDATED_USER, userPayload);

                return userDTO;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats(Long userId) {
        UserEntity user = findUserById(userId);
        log.debug("Fetching stats for user ID: {}", user.getId());

        long topicsCount = topicServiceClient.getTopicCountByUser(user.getId()).count();
        long repliesCount = replyServiceClient.getUserReplyCount(user.getId()).count();
        long followedTopicsCount = topicServiceClient.getUserFollowedTopicCount(user.getId()).count();
        return new UserStatsDTO(topicsCount, repliesCount, followedTopicsCount);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(Long userId) {
        UserEntity user = findUserById(userId);
        log.debug("Fetching authenticated user data. User ID: {}", user.getId());
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        UserEntity user = findUserById(userId);
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
    public UserDTO validateCredentials(LoginUserDTO data) {
        UserEntity user = userRepository.findByEmailOrUsername(data.username(), data.username())
                .orElseThrow(() -> {
                    log.warn("User not found with identifier: {}", data.username());
                    return new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            log.warn("Invalid password for user: {}", data.username());
            throw new AppException("Contraseña incorrecta", HttpStatus.UNAUTHORIZED);
        }

        return UserDTO.fromEntity(user);

    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    private UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Email not registered: {}", email);
                    return new AppException("El email no está registrado.", HttpStatus.NOT_FOUND);
                });
    }

    private UserEntity findUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Invalid or expired token");
                    return new AppException("Token inválido o expirado.", HttpStatus.BAD_REQUEST);
                });
    }

    private void validateTokenExpiration(UserEntity user) {
        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for user ID: {}", user.getId());
            throw new AppException("El token de confirmación ha expirado.", HttpStatus.GONE);
        }
    }

    private void checkIfAccountConfirmed(UserEntity user) {
        if (user.isAccountConfirmed()) {
            log.warn("Account already confirmed for user ID: {}", user.getId());
            throw new AppException("La cuenta ya está confirmada.", HttpStatus.CONFLICT);
        }
    }

    private void checkIfAccountNotConfirmed(UserEntity user) {
        if (!user.isAccountConfirmed()) {
            log.warn("Account not yet confirmed for user ID: {}", user.getId());
            throw new AppException("La cuenta no está confirmada.", HttpStatus.CONFLICT);
        }
    }

    private boolean isRecentRequest(LocalDateTime lastUpdated) {
        return ChronoUnit.MINUTES.between(lastUpdated, LocalDateTime.now()) < 2;
    }

    private void validatePasswordsMatch(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            log.warn("Passwords do not match");
            throw new AppException("Los passwords no coinciden.", HttpStatus.BAD_REQUEST);
        }
    }

    private void existsByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already registered: {}", username);
            throw new AppException("El nombre de usuario ya está registrado.", HttpStatus.CONFLICT);
        }
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            log.warn("Email already registered: {}", email);
            throw new AppException("El email ya está registrado.", HttpStatus.CONFLICT);
        }
    }
    private void validateUsernameContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Username not approved: {}", validationResponse.result());
            throw new AppException("El nombre de usuario " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

}
