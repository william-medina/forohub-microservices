package com.williammedina.user_service.domain.user.service.validator;

import com.williammedina.user_service.domain.user.dto.ContentValidationResponse;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.enums.RequestType;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void ensureTokenIsNotExpired(UserEntity user) {
        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for user ID: {}", user.getId());
            throw new AppException("El token de confirmación ha expirado.", HttpStatus.GONE);
        }
    }

    @Override
    public void ensureAccountIsNotConfirmed(UserEntity user) {
        if (user.isAccountConfirmed()) {
            log.warn("Account already confirmed for user ID: {}", user.getId());
            throw new AppException("La cuenta ya está confirmada.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void ensureAccountIsConfirmed(UserEntity user) {
        if (!user.isAccountConfirmed()) {
            log.warn("Account not yet confirmed for user ID: {}", user.getId());
            throw new AppException("La cuenta no está confirmada.", HttpStatus.CONFLICT);
        }
    }


    @Override
    public void ensurePasswordsMatch(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            log.warn("Passwords do not match");
            throw new AppException("Los passwords no coinciden.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void ensureUsernameIsUnique(String username) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already registered: {}", username);
            throw new AppException("El nombre de usuario ya está registrado.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void ensureEmailIsUnique(String email) {
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            log.warn("Email already registered: {}", email);
            throw new AppException("El email ya está registrado.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void ensureUsernameContentIsValid(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Username not approved: {}", validationResponse.result());
            throw new AppException("El nombre de usuario " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void ensureUsernameIsDifferent(UserEntity user, String newUsername) {
        if (user.getUsername().equals(newUsername)) {
            log.warn("Attempt to update to same username - user ID: {}", user.getId());
            throw new AppException("Debes ingresa un nuevo nombre.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void ensureCurrentPasswordIsValid(UserEntity user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Incorrect current password for user ID: {}", user.getId());
            throw new AppException("El password actual es incorrecto.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void ensureRequestIntervalIsAllowed(UserEntity user, RequestType type) {
        if (isRecentRequest(user.getUpdatedAt()) && user.getToken() != null) {
            switch (type) {
                case CONFIRMATION -> {
                    log.warn("Confirmation request too soon for user ID: {}", user.getId());
                    throw new AppException("Debe esperar 2 minutos para solicitar otro código de confirmación.", HttpStatus.BAD_REQUEST);
                }
                case PASSWORD_RESET -> {
                    log.warn("Password reset request too soon for user ID: {}", user.getId());
                    throw new AppException("Debe esperar 2 minutos para solicitar otro código de restablecimiento de password.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    private boolean isRecentRequest(LocalDateTime lastUpdated) {
        return ChronoUnit.MINUTES.between(lastUpdated, LocalDateTime.now()) < 2;
    }

}
