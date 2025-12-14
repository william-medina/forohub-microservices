package com.williammedina.user_service.domain.user.service.account;

import com.williammedina.user_service.domain.user.dto.*;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.enums.RequestType;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.domain.user.service.finder.UserFinder;
import com.williammedina.user_service.domain.user.service.validator.UserValidator;
import com.williammedina.user_service.infrastructure.client.ContentValidationClient;
import com.williammedina.user_service.infrastructure.event.model.UserEventType;
import com.williammedina.user_service.infrastructure.event.model.UserPayload;
import com.williammedina.user_service.infrastructure.event.publisher.UserEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Service
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContentValidationClient contentValidationClient;
    private final UserEventPublisher userEventPublisher;
    private final UserValidator validator;
    private final UserFinder userFinder;

    @Override
    @Transactional
    public Mono<UserDTO> createAccount(CreateUserDTO request) {
        log.info("Creating account for: {}", request.username());

        validator.ensurePasswordsMatch(request.password(), request.password_confirmation());
        validator.ensureUsernameIsUnique(request.username());
        validator.ensureEmailIsUnique(request.email());

        return contentValidationClient.validateUsername(request.username())
            .publishOn(Schedulers.boundedElastic())
            .map(contentResult -> {

                validator.ensureUsernameContentIsValid(contentResult); // Validate the username using AI

                UserEntity newUserEntity = new UserEntity(request.username(), request.email().trim().toLowerCase(), passwordEncoder.encode(request.password()));
                UserEntity userCreated = userRepository.save(newUserEntity);

                log.info("User created successfully - ID: {}", userCreated.getId());
                UserDTO userDTO = UserDTO.fromEntity(userCreated);
                UserPayload userPayload = new UserPayload(userDTO, userCreated.getEmail(), userCreated.getToken());
                userEventPublisher.publishEvent(UserEventType.CREATED_ACCOUNT, userPayload);

                return userDTO;
            });
    }

    @Override
    @Transactional
    public UserDTO confirmAccount(String token) {
        log.info("Confirming account with token");

        UserEntity user = userFinder.findUserByValidToken(token);
        validator.ensureTokenIsNotExpired(user);
        validator.ensureAccountIsNotConfirmed(user);

        user.setAccountConfirmed(true);
        user.clearTokenData();
        userRepository.save(user);
        log.info("Account confirmed for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDTO requestConfirmationCode(EmailUserDTO request) {
        log.info("Requesting confirmation code for email: {}", request.email());

        UserEntity user = userFinder.findUserByEmail(request.email());
        validator.ensureAccountIsNotConfirmed(user);
        validator.ensureRequestIntervalIsAllowed(user, RequestType.CONFIRMATION);

        user.generateConfirmationToken();
        log.info("Confirmation code generated and sent for user ID: {}", user.getId());

        UserDTO userDTO = UserDTO.fromEntity(user);
        UserPayload userPayload = new UserPayload(userDTO, null, user.getToken());
        userEventPublisher.publishEvent(UserEventType.REQUEST_CONFIRMATION_CODE, userPayload);

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO forgotPassword(EmailUserDTO request) {
        log.info("Password reset requested for: {}", request.email());

        UserEntity user = userFinder.findUserByEmail(request.email());
        validator.ensureAccountIsConfirmed(user);
        validator.ensureRequestIntervalIsAllowed(user, RequestType.PASSWORD_RESET);

        user.generateConfirmationToken();
        log.info("Password reset code generated and sent for user ID: {}", user.getId());

        UserDTO userDTO = UserDTO.fromEntity(user);
        UserPayload userPayload = new UserPayload(userDTO, null, user.getToken());
        userEventPublisher.publishEvent(UserEventType.RESET_PASSWORD, userPayload);

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO request) {
        log.info("Updating password using token");

        validator.ensurePasswordsMatch(request.password(), request.password_confirmation());

        UserEntity user = userFinder.findUserByValidToken(token);
        validator.ensureTokenIsNotExpired(user);
        validator.ensureAccountIsConfirmed(user);

        user.setPassword(passwordEncoder.encode(request.password()));
        user.clearTokenData();
        userRepository.save(user);
        log.info("Password updated for user ID: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

}
