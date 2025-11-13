package com.williammedina.email_service.domain.usercontact.repository;

import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContactEntity, Long> {
}
