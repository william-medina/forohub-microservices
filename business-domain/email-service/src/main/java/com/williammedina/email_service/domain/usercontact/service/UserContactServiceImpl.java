package com.williammedina.email_service.domain.usercontact.service;

import com.williammedina.email_service.domain.email.dto.UserDTO;
import com.williammedina.email_service.domain.usercontact.entity.UserContactEntity;
import com.williammedina.email_service.domain.usercontact.repository.UserContactRepository;
import com.williammedina.email_service.domain.usercontact.service.finder.UserContactFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService {

    private final UserContactRepository userContactRepository;
    private final UserContactFinder userContactFinder;

    @Override
    public UserContactEntity createUserContact(UserDTO user, String email) {
        UserContactEntity userToSave = new UserContactEntity(user.id(), user.username(), email);
        return userContactRepository.save(userToSave);
    }

    @Override
    public void updateUserContact(UserDTO user) {
        UserContactEntity userToUpdate = userContactFinder.findUserById(user.id());
        userToUpdate.setUsername(user.username());
        userContactRepository.save(userToUpdate);
    }

}
