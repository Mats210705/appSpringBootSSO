package com.orbit.sso.service;

import com.orbit.sso.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    public UserInfoDto extractUserInfo(OidcUser oidcUser) {
        log.debug("Extrayendo info del usuario: {}", oidcUser.getPreferredUsername());

        // Los roles vienen en el token JWT, dentro de "realm_access.roles"
        List<String> roles = extractRoles(oidcUser);

        return UserInfoDto.builder()
                .username(oidcUser.getPreferredUsername())
                .email(oidcUser.getEmail())
                .fullName(oidcUser.getFullName())
                .roles(roles)
                .build();
    }

    private List<String> extractRoles(OidcUser oidcUser) {
        try {
            Map<String, Object> realmAccess = oidcUser.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                return (List<String>) realmAccess.get("roles");
            }
        } catch (Exception e) {
            log.warn("No se pudieron extraer roles del token", e);
        }
        return List.of();
    }
}
