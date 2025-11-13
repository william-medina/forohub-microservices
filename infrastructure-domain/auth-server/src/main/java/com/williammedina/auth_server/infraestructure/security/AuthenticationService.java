package com.williammedina.auth_server.infraestructure.security;

import com.williammedina.auth_server.domain.dto.LoginUserDTO;
import com.williammedina.auth_server.domain.dto.UserDTO;
import com.williammedina.auth_server.infraestructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * AuthenticationService:
 * ----------------------
 * Servicio que implementa UserDetailsService para integrar con Spring Security.
 *
 * Función principal:
 * - Proveer métodos para autenticar usuarios usando UserServiceClient.
 * - Se puede usar en flujos OAuth2 o cualquier autenticación que necesite UserDetails.
 *
 * Metodo principal:
 * - authenticateUser(username, password): valida las credenciales y retorna un UserDTO.
 *
 * Nota importante:
 * - loadUserByUsername no está soportado, ya que no tenemos la password en este punto.
 * - Se recomienda usar authenticateUser para flujos que requieran verificación de credenciales.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    /**
     * Este metodo no se usa porque la autenticación se realiza
     * mediante FeignAuthProvider que delega al UserService.
     * Lanzamos UnsupportedOperationException para dejar claro que no debe llamarse.
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) {
        throw new UnsupportedOperationException("Use authenticateUser(username, password) instead");
    }


    /**
     * Valida las credenciales de un usuario contra el microservicio de usuarios.
     */
    public UserDTO authenticateUser(String username, String password) {
        LoginUserDTO loginDTO = new LoginUserDTO(username, password);

        UserDTO user;
        try {
            user = userServiceClient.validateCredentials(loginDTO);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Credenciales inválidas", e);
        }

        return user;
    }

}
