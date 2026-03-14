package nbcc.resto.config;

import nbcc.auth.config.AuthClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reads auth.rest.api.url and auth.rest.api.port from application.properties
 * and provides them to UserClientImpl so it knows where the auth server is.
 */
@Component
public class AuthClientConfigImpl implements AuthClientConfig {

    @Value("${auth.rest.api.url}")
    private String baseUrl;

    @Value("${auth.rest.api.port}")
    private String port;

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getPort() {
        return port;
    }
}