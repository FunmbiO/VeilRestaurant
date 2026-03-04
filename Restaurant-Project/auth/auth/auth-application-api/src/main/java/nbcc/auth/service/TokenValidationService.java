package nbcc.auth.service;

import nbcc.auth.domain.JWTClaims;
import nbcc.auth.result.AuthResult;

public interface TokenValidationService {
    AuthResult<JWTClaims> validateToken(String token);
}
