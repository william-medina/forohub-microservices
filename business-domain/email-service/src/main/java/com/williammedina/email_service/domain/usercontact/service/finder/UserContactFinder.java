package com.williammedina.email_service.domain.usercontact.service.finder;

import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;

public interface UserContactFinder {

    UserContactEntity findUserById(Long userId);

}
