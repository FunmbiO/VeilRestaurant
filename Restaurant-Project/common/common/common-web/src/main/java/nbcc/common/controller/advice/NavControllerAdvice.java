package nbcc.common.controller.advice;

import nbcc.common.service.LoginService;
import nbcc.common.viewmodel.NavViewModel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavControllerAdvice {

    private final LoginService loginService;

    public NavControllerAdvice(LoginService loginService) {
        this.loginService = loginService;
    }

    @ModelAttribute("navViewModel")
    public NavViewModel navViewModel(){
        return new NavViewModel(loginService.isLoggedIn(), loginService.getCurrentUsername());
    }
}
