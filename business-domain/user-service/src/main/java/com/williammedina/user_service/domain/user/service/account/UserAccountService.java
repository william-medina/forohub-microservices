package com.williammedina.user_service.domain.user.service.account;

import com.williammedina.user_service.domain.user.dto.*;
import reactor.core.publisher.Mono;

public interface UserAccountService {

    Mono<UserDTO> createAccount(CreateUserDTO data);
    UserDTO confirmAccount(String token);
    UserDTO requestConfirmationCode(EmailUserDTO data);
    UserDTO forgotPassword(EmailUserDTO data);
    UserDTO updatePasswordWithToken(String token, UpdatePasswordWithTokenDTO data);

}
