package com.williammedina.auth_server.infraestructure.client;

import com.williammedina.auth_server.domain.dto.LoginUserDTO;
import com.williammedina.auth_server.domain.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/internal/auth")
public interface UserServiceClient {

    @PostMapping("/validate-credentials")
    UserDTO validateCredentials(@RequestBody LoginUserDTO data);

}
