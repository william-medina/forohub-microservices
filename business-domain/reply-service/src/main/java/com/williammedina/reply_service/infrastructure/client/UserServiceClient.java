package com.williammedina.reply_service.infrastructure.client;

import com.williammedina.reply_service.domain.reply.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/internal/auth")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long id);

    @GetMapping("/batch")
    List<UserDTO> getUsersByIds(@RequestParam List<Long> ids);

}
