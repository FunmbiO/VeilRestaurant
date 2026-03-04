package nbcc.auth.client;

import nbcc.auth.config.AuthClientConfig;
import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.result.AuthClientResult;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Primary
@Component
public class AuthClientImpl implements AuthClient {

    private static final String basepath = "/api/auth";

    private final RestClient restClient;
    private final AuthClientConfig config;

    private final Logger logger = LoggerFactory.getLogger(AuthClientImpl.class);

    public AuthClientImpl(RestClient.Builder builder, AuthClientConfig config) {
        this.restClient = builder.build();
        this.config = config;
    }

    @Override
    public AuthResult<BearerToken> createToken(LoginRequest loginRequest) {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(config.getBaseAddress())
                    .path(basepath)
                    .path("/token")
                    .build()
                    .toUri();

            logger.debug("Creating token for user {} with URI {}", loginRequest.getUsername(), uri);
            var response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<BearerToken>>() {
                    });

            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.error("Failed to create token for user {}", loginRequest.getUsername(), e);
            return AuthClientResult.response(e, BearerToken.class);

        } catch (Exception e) {
            logger.error("Failed to create token for user {}:", loginRequest.getUsername(), e);
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<JWTClaims> validateToken(String token) {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(config.getBaseAddress())
                    .path(basepath)
                    .path("/validate")
                    .build()
                    .toUri();

            logger.debug("validating token with URI {}", uri);
            var response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(token)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<JWTClaims>>() {
                    });

            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.error("Failed to validate token", e);
            return AuthClientResult.response(e, JWTClaims.class);

        } catch (Exception e) {
            logger.error("Failed to validate token: {}", e.getMessage());
            return AuthClientResult.error(e);
        }
    }
}