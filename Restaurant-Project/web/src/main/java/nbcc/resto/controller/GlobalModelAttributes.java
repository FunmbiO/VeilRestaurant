package nbcc.resto.controller;

import nbcc.auth.security.AppUserDetails;
import nbcc.resto.service.SuggestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final SuggestionService suggestionService;

    public GlobalModelAttributes(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(@AuthenticationPrincipal AppUserDetails currentUser) {
        return currentUser != null && currentUser.isAdmin();
    }

    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn(@AuthenticationPrincipal AppUserDetails currentUser) {
        return currentUser != null;
    }

    @ModelAttribute("currentUserId")
    public Long currentUserId(@AuthenticationPrincipal AppUserDetails currentUser) {
        return currentUser != null ? currentUser.getId() : null;
    }

    @ModelAttribute("inboxCount")
    public long inboxCount(@AuthenticationPrincipal AppUserDetails currentUser) {
        if (currentUser == null || currentUser.isAdmin()) return 0;
        return suggestionService.countUnreadForCreator(currentUser.getId());
    }
}