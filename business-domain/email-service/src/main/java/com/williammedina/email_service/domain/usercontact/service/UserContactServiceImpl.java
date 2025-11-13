package com.williammedina.email_service.domain.usercontact.service;

import com.williammedina.email_service.domain.email.dto.UserDTO;
import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;
import com.williammedina.email_service.domain.usercontact.repository.UserContactRepository;
import com.williammedina.email_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService, UserContactQueryService {

    private final UserContactRepository userContactRepository;

    @Override
    public UserContactEntity createUserContact(UserDTO user, String email) {
        UserContactEntity userToSave = new UserContactEntity(user.id(), user.username(), email);
        return userContactRepository.save(userToSave);
    }

    @Override
    public void updateUserContact(UserDTO user) {
        UserContactEntity userToUpdate = findUserById(user.id());
        userToUpdate.setUsername(user.username());
        userContactRepository.save(userToUpdate);
    }

    @Override
    public UserContactEntity findUserById(Long userId) {
        return userContactRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
                });
    }
}
