package nbcc.auth.service;


import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.result.AuthResult;

public interface TokenCreationService {

    AuthResult<BearerToken> createToken(UserResponse userResponse);
}
