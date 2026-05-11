package com.orbit.sso.controller;

import org.springframework.ui.Model;
import com.orbit.sso.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String index() {
        return "index";  // página de login
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("user", userService.extractUserInfo(oidcUser));
        return "dashboard";
    }
}