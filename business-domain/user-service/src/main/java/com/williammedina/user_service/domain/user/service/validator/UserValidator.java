package com.williammedina.user_service.domain.user.service.validator;

import com.williammedina.user_service.domain.user.dto.ContentValidationResponse;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.enums.RequestType;

public interface UserValidator {

    void ensureTokenIsNotExpired(UserEntity user);
    void ensureAccountIsNotConfirmed(UserEntity user);
    void ensureAccountIsConfirmed(UserEntity user);
    void ensurePasswordsMatch(String password, String passwordConfirmation);
    void ensureUsernameIsUnique(String username);
    void ensureEmailIsUnique(String email);
    void ensureUsernameContentIsValid(ContentValidationResponse validationResponse);
    void ensureUsernameIsDifferent(UserEntity user, String newUsername);
    void ensureCurrentPasswordIsValid(UserEntity user, String currentPassword);
    void ensureRequestIntervalIsAllowed(UserEntity user, RequestType type);

}
