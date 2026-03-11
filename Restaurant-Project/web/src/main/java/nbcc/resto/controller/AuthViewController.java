package nbcc.resto.controller;

import nbcc.auth.domain.LoginRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.service.UserService;
import nbcc.auth.config.BearerTokenConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Handles login and registration views.
 *
 * Login:    GET  /login  -> shows login form
 *           POST /login  -> calls auth server, stores token, sets security context
 * Register: GET  /register -> shows register form
 *           POST /register -> calls auth server to create user
 */
@Controller
public class AuthViewController {

    private final UserService userService = null;
    private final BearerTokenConfig bearerTokenConfig;

    public AuthViewController(BearerTokenConfig bearerTokenConfig) {
        this.bearerTokenConfig = bearerTokenConfig;
    }

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out.");
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, Model model) {
        var result = userService.isAuthorized(loginRequest);

        if (result.isError()) {
            model.addAttribute("errorMessage", "An error occurred. Please try again.");
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }

        if (result.isInvalid() || result.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid username or password.");
            model.addAttribute("loginRequest", loginRequest);
            return "auth/login";
        }

        // Set Spring Security context so the app knows the user is logged in
        var user = result.getValue();
        var authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/events";
    }

    // -------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistration userRegistration, Model model) {
        var result = userService.register(userRegistration);

        if (result.isError()) {
            model.addAttribute("errorMessage", "An error occurred. Please try again.");
            model.addAttribute("userRegistration", userRegistration);
            return "auth/register";
        }

        if (result.isInvalid()) {
            model.addAttribute("errorMessage", "Registration failed. Please check your details.");
            model.addAttribute("userRegistration", userRegistration);
            return "auth/register";
        }

        return "redirect:/login?registered=true";
    }
}
