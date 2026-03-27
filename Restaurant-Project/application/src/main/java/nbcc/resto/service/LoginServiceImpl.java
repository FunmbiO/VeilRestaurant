package nbcc.resto.service;

import nbcc.auth.config.BearerTokenConfig;
import nbcc.common.service.LoginService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implements LoginService using Spring Security's context.
 * Used by controllers and Thymeleaf templates to check login state
 * and get the current username.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final BearerTokenConfig bearerTokenConfig;

    public LoginServiceImpl(BearerTokenConfig bearerTokenConfig) {
        this.bearerTokenConfig = bearerTokenConfig;
    }

    @Override
    public boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
    }

    @Override
    public boolean isLoggedOut() {
        return !isLoggedIn();
    }

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }
}
