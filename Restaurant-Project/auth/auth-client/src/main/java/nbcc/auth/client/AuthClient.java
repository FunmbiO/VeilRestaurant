package nbcc.auth.client;

import nbcc.auth.domain.*;
import nbcc.auth.result.AuthResult;

public interface AuthClient {

    AuthResult<BearerToken> createToken(LoginRequest loginRequest);

    AuthResult<JWTClaims> validateToken(String token);

}
