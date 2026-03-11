package nbcc.resto.config;

import nbcc.auth.config.BearerTokenConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Stores the JWT bearer token for the currently logged-in user.
 * SessionScope means each user gets their own instance.
 */
@Component
@SessionScope
public class BearerTokenConfigImpl implements BearerTokenConfig {

    private String token;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    public boolean hasToken() {
        return token != null && !token.isBlank();
    }
}
