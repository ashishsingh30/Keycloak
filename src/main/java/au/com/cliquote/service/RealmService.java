package au.com.cliquote.service;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RealmService {

    public boolean isAuthorizedForRealm(String targetRealm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof KeycloakSecurityContext)) {
            return false;
        }

        KeycloakSecurityContext context = (KeycloakSecurityContext) authentication.getPrincipal();
        String userRealm = context.getRealm();
        return userRealm.equals(targetRealm);
    }
}


