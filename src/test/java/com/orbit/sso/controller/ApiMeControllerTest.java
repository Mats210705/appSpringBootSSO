package com.orbit.sso.controller;

import com.orbit.sso.dto.UserInfoDto;
import com.orbit.sso.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiMeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnUserInfoWhenAuthenticated() throws Exception {

        UserInfoDto expectedDto = UserInfoDto.builder()
                .username("juanperez")
                .email("juan@test.com")
                .fullName("Juan Pérez")
                .roles(List.of("ROLE_USER"))
                .build();

        when(userService.extractUserInfo(any(OidcUser.class)))
                .thenReturn(expectedDto);

        mockMvc.perform(get("/api/me")
                        .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                .idToken(token -> token
                                        .subject("12345")
                                        .claim("preferred_username", "juanperez")
                                        .claim("email", "juan@test.com")
                                        .claim("name", "Juan Pérez"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("juanperez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
    }
    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    void shouldLogoutCorrectly() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(SecurityMockMvcRequestPostProcessors.oidcLogin())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}