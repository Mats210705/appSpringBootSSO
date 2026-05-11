package com.orbit.sso.controller;

import com.orbit.sso.dto.UserInfoDto;
import com.orbit.sso.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Test
    void index_debeRetornarVistaIndex() {
        String vista = authController.index();
        assertThat(vista).isEqualTo("index");
    }

    @Test
    void dashboard_debeRetornarVistaDashboard() {
        // Arrange
        OidcUser mockUser = mock(OidcUser.class);
        UserInfoDto dto = UserInfoDto.builder()
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .roles(List.of("user"))
                .build();

        when(userService.extractUserInfo(mockUser)).thenReturn(dto);

        // Act
        String vista = authController.dashboard(model, mockUser);

        // Assert
        assertThat(vista).isEqualTo("dashboard");
        verify(model).addAttribute(eq("user"), eq(dto));
    }

    @Test
    void dashboard_debeAgregarUsuarioAlModelo() {
        // Arrange
        OidcUser mockUser = mock(OidcUser.class);
        UserInfoDto dto = UserInfoDto.builder()
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .roles(List.of("admin"))
                .build();

        when(userService.extractUserInfo(mockUser)).thenReturn(dto);

        // Act
        authController.dashboard(model, mockUser);

        // Assert
        verify(userService).extractUserInfo(mockUser);
        verify(model).addAttribute("user", dto);
    }

}