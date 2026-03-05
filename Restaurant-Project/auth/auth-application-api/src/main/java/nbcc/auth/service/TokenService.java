package nbcc.auth.service;

import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.result.AuthResult;

public interface TokenService {
    AuthResult<BearerToken> createToken(LoginRequest loginRequest);

    AuthResult<JWTClaims> validateToken(String token);
}
