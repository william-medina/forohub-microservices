package com.williammedina.user_service.domain.user.repository;

import com.williammedina.user_service.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailOrUsername(String email, String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<UserEntity> findByToken(String token);
}
