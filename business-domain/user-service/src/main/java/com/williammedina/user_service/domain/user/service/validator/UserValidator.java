package com.williammedina.user_service.domain.user.service.validator;

import com.williammedina.user_service.domain.user.dto.ContentValidationResponse;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.enums.RequestType;

public interface UserValidator {

    void validateTokenExpiration(UserEntity user);
    void checkIfAccountConfirmed(UserEntity user);
    void checkIfAccountNotConfirmed(UserEntity user);
    void validatePasswordsMatch(String password, String passwordConfirmation);
    void existsByUsername(String username);
    void existsByEmail(String email);
    void validateUsernameContent(ContentValidationResponse validationResponse);
    void ensureNewUsername(UserEntity user, String newUsername);
    void validateCurrentPassword(UserEntity user, String currentPassword);
    void ensureAllowedRequestInterval(UserEntity user, RequestType type);

}
