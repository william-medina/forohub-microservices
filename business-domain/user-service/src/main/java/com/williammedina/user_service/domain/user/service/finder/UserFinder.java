package com.williammedina.user_service.domain.user.service.finder;

import com.williammedina.user_service.domain.user.entity.UserEntity;

public interface UserFinder {

    UserEntity findUserByEmailOrUsername(String email, String username);
    UserEntity findUserById(Long userId);
    UserEntity findUserByEmail(String email);
    UserEntity findUserByValidToken(String token);

}
