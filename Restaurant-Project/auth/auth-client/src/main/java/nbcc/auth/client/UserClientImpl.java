
package nbcc.auth.client;

import nbcc.auth.config.BearerTokenConfig;
import nbcc.auth.config.AuthClientConfig;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.result.AuthClientResult;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class UserClientImpl implements UserClient {

    private static final String basepath = "/api/user";

    private final RestClient restClient;
    private final AuthClientConfig config;
    private final BearerTokenConfig bearerTokenConfig;

    private final Logger logger = LoggerFactory.getLogger(UserClientImpl.class);

    public UserClientImpl(RestClient.Builder builder, AuthClientConfig config, BearerTokenConfig bearerTokenConfig) {
        this.restClient = builder.build();
        this.config = config;
        this.bearerTokenConfig = bearerTokenConfig;
    }

    @Override
    public AuthResult<UserResponse> register(UserRegistration userRegistration) {
        try {
            var uri = new URI(config.getBaseAddress() + basepath + "/register");
            logger.debug("registering with URI {}", uri);
            var response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRegistration)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<UserResponse>>() {
                    });

            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.error("Failed to register user {}", userRegistration.getUsername(), e);
            return AuthClientResult.response(e, UserResponse.class);

        } catch (Exception e) {
            logger.error("Failed to register user: {}", e.getMessage());
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> isAuthorized(LoginRequest loginRequest) {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(config.getBaseAddress())
                    .path(basepath)
                    .path("/authorize")
                    .build()
                    .toUri();

            logger.debug("authorising with URI {}", uri);
            var response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<UserResponse>>() {
                    });

            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.error("Failed to register user {}", loginRequest.getUsername(), e);
            return AuthClientResult.response(e, UserResponse.class);

        } catch (Exception e) {
            logger.error("Failed to retrieve UserResponse to authorize user", e);
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> getUser(String username) {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(config.getBaseAddress())
                    .path(basepath)
                    .path("/user/{username}")
                    .buildAndExpand(username)
                    .toUri();

            logger.debug("retrieving user {} with URI {}", username, uri);
            var response = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + bearerTokenConfig.getToken())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<UserResponse>>() {
                    });

            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.info("unsuccessful User retrieval: {}", username);
            return AuthClientResult.response(e, UserResponse.class);

        } catch (Exception e) {
            logger.error("Failed to retrieve User {}", username, e);
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> getProfile() {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(config.getBaseAddress())
                    .path(basepath)
                    .path("/profile")
                    .build()
                    .toUri();

            logger.debug("retrieving profile with URI {}", uri);
            var response = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + bearerTokenConfig.getToken())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<AuthClientResult<UserResponse>>() {
                    });


            return AuthClientResult.response(response);

        } catch (HttpClientErrorException e) {
            logger.info("unsuccessful profile retrieval");
            return AuthClientResult.response(e, UserResponse.class);

        } catch (Exception e) {
            logger.error("Failed to retrieve profile", e);
            return AuthClientResult.error(e);
        }
    }
}