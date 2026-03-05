package nbcc.auth.controller;

import nbcc.auth.domain.*;
import nbcc.auth.result.AuthResult;
import nbcc.auth.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static nbcc.auth.result.ResultHandler.handleResult;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthResult<UserResponse>> authorize(@RequestBody LoginRequest loginRequest) {

        var result = userService.isAuthorized(loginRequest);

        if(result.isError()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(result.isInvalid() || result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResult<UserResponse>> register(@RequestBody UserRegistration userRegistration) {
        var result = userService.register(userRegistration);
        return handleResult(result, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<AuthResult<UserResponse>> profile(@AuthenticationPrincipal Jwt jwt) {

        String userName = jwt.getClaim(JWTClaims.USERNAME);

        var result = userService.get(userName);
        return handleResult(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/details/{username}")
    public ResponseEntity<AuthResult<UserResponse>> getUser(@PathVariable String username) {

        var result = userService.get(username);
        return handleResult(result, HttpStatus.OK);
    }
}
