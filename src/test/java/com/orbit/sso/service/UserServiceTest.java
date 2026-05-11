package com.orbit.sso.service;

import com.orbit.sso.dto.UserInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Test
    void extractUserInfo_deberiaRetornarDatosCorrectos() {
        // Arrange
        OidcUser mockUser = mock(OidcUser.class);
        when(mockUser.getPreferredUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getFullName()).thenReturn("Test User");
        when(mockUser.getClaimAsMap("realm_access"))
                .thenReturn(Map.of("roles", List.of("user", "admin")));

        UserInfoDto result = userService.extractUserInfo(mockUser);

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFullName()).isEqualTo("Test User");
        assertThat(result.getRoles()).containsExactlyInAnyOrder("user", "admin");
    }

    @Test
    void extractUserInfo_sinRoles_deberiaRetornarListaVacia() {
        // Arrange
        OidcUser mockUser = mock(OidcUser.class);
        when(mockUser.getPreferredUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getFullName()).thenReturn("Test User");
        when(mockUser.getClaimAsMap("realm_access")).thenReturn(null);

        // Act
        UserInfoDto result = userService.extractUserInfo(mockUser);

        // Assert
        assertThat(result.getRoles()).isEmpty();
    }

    @Test
    void extractUserInfo_realmAccessSinCampoRoles_deberiaRetornarListaVacia() {
        // Arrange
        OidcUser mockUser = mock(OidcUser.class);
        when(mockUser.getPreferredUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getFullName()).thenReturn("Test User");
        when(mockUser.getClaimAsMap("realm_access"))
                .thenReturn(Map.of("otherKey", "otherValue"));


        UserInfoDto result = userService.extractUserInfo(mockUser);


        assertThat(result.getRoles()).isEmpty();
    }
}