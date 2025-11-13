package com.williammedina.notification_service.infrastructure.client;

import com.williammedina.notification_service.domain.notification.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/auth")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long id);

}
