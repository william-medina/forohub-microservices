package com.williammedina.email_service.domain.usercontact.service;

import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;

public interface UserContactQueryService {

    UserContactEntity findUserById(Long userId);

}
