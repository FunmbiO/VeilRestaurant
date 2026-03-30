package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.auth.security.AppUserDetails;
import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.dto.EventSearchCriteria;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.service.EventService;
import nbcc.resto.service.MenuService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;

@Controller
public class EventViewController {

    private final EventService eventService;
    private final MenuService menuService;

    public EventViewController(EventService eventService, MenuService menuService) {
        this.eventService = eventService;
        this.menuService  = menuService;
    }

    // LIST  GET /events
    @GetMapping("/events")
    public String listEvents(@ModelAttribute EventSearchCriteria criteria,
                             @AuthenticationPrincipal AppUserDetails currentUser,
                             Model model) {
        boolean searchActive = isSearchActive(criteria);
        List<EventDTO> events = searchActive
                ? eventService.searchEvents(criteria)
                : eventService.getAllActiveEvents();

        model.addAttribute("events", events);
        model.addAttribute("criteria", criteria);
        model.addAttribute("searchActive", searchActive);
        model.addAttribute("resultCount", events.size());
        return "events/list";
    }

    private boolean isSearchActive(EventSearchCriteria criteria) {
        boolean hasName   = criteria.getName() != null && !criteria.getName().isBlank();
        boolean hasDate   = criteria.getStartDate() != null || criteria.getEndDate() != null;
        boolean hasFilter = criteria.getDateRangeFilter() != null
                && !criteria.getDateRangeFilter().equals("ALL");
        return hasName || hasDate || hasFilter;
    }

    // DETAIL  GET /events/{id}
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id,
                              @AuthenticationPrincipal AppUserDetails currentUser,
                              Model model) {
        try {
            EventDTO event = eventService.getEventById(id);

            boolean isAdmin   = currentUser != null && currentUser.isAdmin();
            boolean isCreator = currentUser != null
                    && Objects.equals(event.getCreatedBy(), currentUser.getId());
            boolean canEdit    = isAdmin || isCreator;
            boolean canSuggest = currentUser != null && !isCreator;

            model.addAttribute("event", event);
            model.addAttribute("canEdit", canEdit);
            model.addAttribute("canSuggest", canSuggest);
            return "events/detail";

        } catch (EventNotFoundException e) {
            return "redirect:/events";
        }
    }

    // CREATE FORM  GET /events/new
    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createEventRequest", new CreateEventRequest());
        model.addAttribute("menus", menuService.getAllMenus());
        return "events/create";
    }

    // CREATE SUBMIT  POST /events/new
    @PostMapping("/events/new")
    public String submitCreateForm(
            @Valid @ModelAttribute CreateEventRequest createEventRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal AppUserDetails currentUser,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("menus", menuService.getAllMenus());
            return "events/create";
        }

        try {
            Long createdBy = currentUser != null ? currentUser.getId() : null;
            EventDTO created = eventService.createEvent(createEventRequest, createdBy);
            return "redirect:/events/" + created.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("menus", menuService.getAllMenus());
            return "events/create";
        }
    }
}