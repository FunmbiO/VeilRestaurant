package nbcc.auth.service;

import nbcc.auth.client.AuthClient;
import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.result.AuthClientResult;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthClientServiceImpl implements TokenService {

    private final Logger logger = LoggerFactory.getLogger(AuthClientServiceImpl.class);

    private final AuthClient authClient;

    public AuthClientServiceImpl(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public AuthResult<BearerToken> createToken(LoginRequest loginRequest) {
        try {
            return authClient.createToken(loginRequest);
        } catch (Exception e) {
            logger.error("Error creating token for user: {}", loginRequest.getUsername(), e);
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<JWTClaims> validateToken(String token) {
        try {
            return authClient.validateToken(token);
        } catch (Exception e) {
            logger.error("Error validating token", e);
            return AuthClientResult.error(e);
        }
    }
}
