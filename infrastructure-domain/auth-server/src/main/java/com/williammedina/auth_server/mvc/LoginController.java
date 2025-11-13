package com.williammedina.auth_server.mvc;

import com.williammedina.auth_server.infraestructure.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AppProperties appProperties;

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        SavedRequest savedRequest = (SavedRequest) request.getSession()
                .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (savedRequest == null) {
            return "redirect:" + appProperties.getFrontendUrl() + "/login";
        }

        return "login";
    }
}

