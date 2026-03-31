package nbcc.resto.controller;

import nbcc.auth.security.AppUserDetails;
import nbcc.resto.dto.SuggestionDTO;
import nbcc.resto.entity.Suggestion.Priority;
import nbcc.resto.entity.Suggestion.TargetType;
import nbcc.resto.service.SuggestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    // SUGGEST FORM  GET /events/{id}/suggest
    @GetMapping("/events/{id}/suggest")
    public String showEventSuggestForm(@PathVariable Long id,
                                       @AuthenticationPrincipal AppUserDetails currentUser,
                                       Model model) {
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("targetType", "EVENT");
        model.addAttribute("targetId", id);
        return "suggestions/form";
    }

    // SUGGEST FORM  GET /menus/{id}/suggest
    @GetMapping("/menus/{id}/suggest")
    public String showMenuSuggestForm(@PathVariable Long id,
                                      @AuthenticationPrincipal AppUserDetails currentUser,
                                      Model model) {
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("targetType", "MENU");
        model.addAttribute("targetId", id);
        return "suggestions/form";
    }

    // SUGGEST SUBMIT  POST /suggestions
    @PostMapping("/suggestions")
    public String submitSuggestion(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @RequestParam String suggestionText,
            @RequestParam String priority,
            @AuthenticationPrincipal AppUserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) return "redirect:/login";

        try {
            suggestionService.createSuggestion(
                    TargetType.valueOf(targetType),
                    targetId,
                    suggestionText,
                    Priority.valueOf(priority),
                    currentUser.getId()
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your suggestion has been submitted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String redirect = targetType.equals("EVENT")
                ? "/events/" + targetId
                : "/menus/" + targetId;
        return "redirect:" + redirect;
    }

    // INBOX  GET /inbox
    @GetMapping("/inbox")
    public String inbox(@AuthenticationPrincipal AppUserDetails currentUser,
                        @RequestParam(required = false, defaultValue = "false") boolean showAll,
                        Model model) {
        if (currentUser == null) return "redirect:/login";
        if (currentUser.isAdmin()) return "redirect:/admin/portal";

        List<SuggestionDTO> suggestions =
                suggestionService.getSuggestionsForCreator(currentUser.getId());

        if (!showAll) {
            suggestions = suggestions.stream()
                    .filter(s -> s.getStatus() ==
                            nbcc.resto.entity.Suggestion.Status.PENDING)
                    .toList();
        }

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("showAll", showAll);
        return "suggestions/inbox";
    }

    // ADMIN PORTAL  GET /admin/portal
    @GetMapping("/admin/portal")
    public String adminPortal(@AuthenticationPrincipal AppUserDetails currentUser,
                              @RequestParam(required = false, defaultValue = "ALL") String filter,
                              Model model) {
        if (currentUser == null || !currentUser.isAdmin())
            return "redirect:/events?error=unauthorized";

        List<SuggestionDTO> suggestions = suggestionService.getAllSuggestionsForAdmin();

        if (!filter.equals("ALL")) {
            nbcc.resto.entity.Suggestion.Status filterStatus =
                    nbcc.resto.entity.Suggestion.Status.valueOf(filter);
            suggestions = suggestions.stream()
                    .filter(s -> s.getStatus() == filterStatus)
                    .toList();
        }

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("filter", filter);
        return "suggestions/admin-portal";
    }

    // FULFILL  POST /admin/suggestions/{id}/fulfill
    @PostMapping("/admin/suggestions/{id}/fulfill")
    public String fulfill(@PathVariable Long id,
                          @AuthenticationPrincipal AppUserDetails currentUser,
                          RedirectAttributes redirectAttributes) {
        if (currentUser == null || !currentUser.isAdmin())
            return "redirect:/events?error=unauthorized";

        try {
            suggestionService.fulfill(id, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Suggestion marked as fulfilled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/portal";
    }

    // DISCARD  POST /admin/suggestions/{id}/discard
    @PostMapping("/admin/suggestions/{id}/discard")
    public String discard(@PathVariable Long id,
                          @AuthenticationPrincipal AppUserDetails currentUser,
                          RedirectAttributes redirectAttributes) {
        if (currentUser == null || !currentUser.isAdmin())
            return "redirect:/events?error=unauthorized";

        try {
            suggestionService.discard(id);
            redirectAttributes.addFlashAttribute("successMessage", "Suggestion discarded.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/portal";
    }

    // RESOLVE BY CREATOR  POST /inbox/suggestions/{id}/resolve
    @PostMapping("/inbox/suggestions/{id}/resolve")
    public String resolveByCreator(@PathVariable Long id,
                                   @AuthenticationPrincipal AppUserDetails currentUser,
                                   RedirectAttributes redirectAttributes) {
        if (currentUser == null) return "redirect:/login";
        if (currentUser.isAdmin()) return "redirect:/admin/portal";

        try {
            suggestionService.resolveByCreator(id, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Suggestion marked as resolved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/inbox";
    }
}