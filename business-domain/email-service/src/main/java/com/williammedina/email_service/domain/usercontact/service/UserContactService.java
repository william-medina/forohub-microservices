package com.williammedina.email_service.domain.usercontact.service;

import com.williammedina.email_service.domain.email.dto.UserDTO;
import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;

public interface UserContactService {

    UserContactEntity createUserContact(UserDTO user, String email);
    void updateUserContact(UserDTO user);

}
