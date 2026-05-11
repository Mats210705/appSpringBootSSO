package com.orbit.sso.controller;

import com.orbit.sso.dto.UserInfoDto;
import com.orbit.sso.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuario", description = "Endpoints de información del usuario autenticado")
public class ApiMeController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario autenticado", description = "Devuelve nombre, email y roles del usuario logueado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserInfoDto> getMe(@AuthenticationPrincipal DefaultOidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Solicitud /api/me del usuario: {}", oidcUser.getPreferredUsername());
        return ResponseEntity.ok(userService.extractUserInfo(oidcUser));
    }
}