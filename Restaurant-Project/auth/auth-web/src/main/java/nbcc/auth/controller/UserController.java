package nbcc.auth.controller;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static nbcc.common.validation.ModelErrorConverter.addErrorsToBindingResults;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) throws Exception {
        model.addAttribute("user", new UserRegistration());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegistration userRegistration, BindingResult br, Model model) {

        var result = userService.register(userRegistration);

        if(result.isError()) {
            return "error/errorPage";
        }

        if (result.isInvalid()) {
            addErrorsToBindingResults(br, result, "user");
            return "register";
        }

        return "redirect:/login";
    }
}