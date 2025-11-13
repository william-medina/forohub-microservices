package com.williammedina.auth_server.infraestructure.security;

import com.williammedina.auth_server.domain.dto.LoginUserDTO;
import com.williammedina.auth_server.domain.dto.UserDTO;
import com.williammedina.auth_server.infraestructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * FeignAuthProvider:
 * ------------------
 * Implementación de AuthenticationProvider de Spring Security.
 * Su función es autenticar usuarios contra un microservicio externo (UserServiceClient) usando Feign.
 *
 * Cómo funciona:
 * 1. Recibe un objeto Authentication con username y password.
 * 2. Llama al UserServiceClient para validar las credenciales.
 * 3. Si las credenciales son válidas, retorna un Authentication con roles asignados (ROLE_USER).
 * 4. Si las credenciales son inválidas, lanza BadCredentialsException.
 *
 * Nota:
 * - Esta clase se integra automáticamente en Spring Security gracias a @Component.
 * - Se usa típicamente en flujos OAuth2 donde el auth-server necesita autenticar usuarios externos.
 */
@Component
@RequiredArgsConstructor
public class FeignAuthProvider implements AuthenticationProvider {

    private final UserServiceClient userServiceClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // Valida las credenciales contra el microservicio de usuarios
            UserDTO user = userServiceClient.validateCredentials(new LoginUserDTO(username, password));

            // Crear objeto serializable
            Map<String, String> principal = new HashMap<>();
            principal.put("id", String.valueOf(user.id()));
            principal.put("username", user.username());
            principal.put("profile", user.profile());

            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

            return new UsernamePasswordAuthenticationToken(principal, null, Collections.singleton(authority));

        } catch (Exception e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
