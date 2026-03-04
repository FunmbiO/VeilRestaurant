package nbcc.auth.service;

import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.result.AuthResult;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final UserService userService;
    private final TokenCreationService tokenCreationService;
    private final TokenValidationService tokenValidationService;

    public TokenServiceImpl(UserService userService, TokenCreationService tokenCreationService, TokenValidationService tokenValidationService) {
        this.userService = userService;
        this.tokenCreationService = tokenCreationService;
        this.tokenValidationService = tokenValidationService;
    }

    @Override
    public AuthResult<BearerToken> createToken(LoginRequest loginRequest) {

        var userResult = userService.isAuthorized(loginRequest);

        if(userResult.isError()){
            return AuthResult.error();
        }

        if(userResult.isInvalid()){
            return AuthResult.invalid(userResult.getValidationErrors());
        }

        if(userResult.isSuccessful() && userResult.hasValue()){
            return tokenCreationService.createToken(userResult.getValue());
        }

        return AuthResult.error();
    }

    @Override
    public AuthResult<JWTClaims> validateToken(String token) {

        return tokenValidationService.validateToken(token);
    }
}
