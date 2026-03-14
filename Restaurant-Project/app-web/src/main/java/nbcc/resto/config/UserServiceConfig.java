package nbcc.resto.config;

import nbcc.auth.client.UserClient;
import nbcc.auth.client.UserClientImpl;
import nbcc.auth.config.AuthClientConfig;
import nbcc.auth.config.BearerTokenConfig;
import nbcc.auth.repository.UserRepository;
import nbcc.auth.repository.UserRepositoryAdapter;
import nbcc.auth.service.UserClientServiceImpl;
import nbcc.auth.service.UserService;
import nbcc.auth.service.UserServiceImpl;
import nbcc.auth.validation.LoginRequestValidator;
import nbcc.auth.validation.UserValidator;
import nbcc.auth.validation.UserValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class UserServiceConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public UserClient userClient(RestClient.Builder builder,
                                 AuthClientConfig authClientConfig,
                                 BearerTokenConfig bearerTokenConfig) {
        return new UserClientImpl(builder, authClientConfig, bearerTokenConfig);
    }

    @Bean
    @Primary
    public UserService userService(UserRepositoryAdapter userRepository,
                                   UserValidatorImpl userValidator,
                                   LoginRequestValidator loginRequestValidator) {
        return new UserServiceImpl(loginRequestValidator, userRepository, userValidator);
    }
}