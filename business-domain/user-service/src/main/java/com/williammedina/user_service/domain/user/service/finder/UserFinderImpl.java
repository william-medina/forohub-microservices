package com.williammedina.user_service.domain.user.service.finder;

import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.repository.UserRepository;
import com.williammedina.user_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFinderImpl implements UserFinder {

    private final UserRepository userRepository;

    @Override
    public UserEntity findUserByEmailOrUsername(String email, String username) {
        return userRepository.findByEmailOrUsername(email, username)
                .orElseThrow(() -> {
                    log.warn("User not found with identifier: {}", username);
                    return new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Email not registered: {}", email);
                    return new AppException("El email no está registrado.", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public UserEntity findUserByValidToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Invalid or expired token");
                    return new AppException("Token inválido o expirado.", HttpStatus.BAD_REQUEST);
                });
    }
}
