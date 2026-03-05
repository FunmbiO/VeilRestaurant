package nbcc.auth.controller;

import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.result.AuthResult;
import nbcc.auth.service.TokenCreationService;
import nbcc.auth.service.TokenValidationService;
import nbcc.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nbcc.auth.result.ResultHandler.handleResult;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final TokenCreationService tokenCreationService;
    private final TokenValidationService tokenValidationService;
    private final UserService userService;

    public AuthApiController(TokenCreationService tokenCreationService, TokenValidationService tokenValidationService, UserService userService) {
        this.tokenCreationService = tokenCreationService;
        this.tokenValidationService = tokenValidationService;
        this.userService = userService;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResult<BearerToken>> createBearerToken(@RequestBody LoginRequest loginRequest) {

        var userResult = userService.isAuthorized(loginRequest);
        if(userResult.isError()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(userResult.isInvalid() || userResult.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        var result = tokenCreationService.createToken(userResult.getValue());
        return handleResult(result, HttpStatus.CREATED, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("validate")
    public ResponseEntity<AuthResult<JWTClaims>> validateToken(@RequestBody String token) {

        var result = tokenValidationService.validateToken(token);
        return handleResult(result, HttpStatus.OK, HttpStatus.UNAUTHORIZED);
    }
}
