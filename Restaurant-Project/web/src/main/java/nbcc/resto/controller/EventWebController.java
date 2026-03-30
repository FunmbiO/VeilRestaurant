package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.auth.security.AppUserDetails;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.dto.UpdateEventRequest;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.exception.UnauthorizedException;
import nbcc.resto.request.UpdateEventWebRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events")
public class EventWebController {

    private final EventService eventService;
    private final MenuService menuService;

    public EventWebController(EventService eventService, MenuService menuService) {
        this.eventService = eventService;
        this.menuService  = menuService;
    }

    // DELETE CONFIRM  GET /events/{id}/delete
    @GetMapping("/{id}/delete")
    public String showDeleteConfirm(@PathVariable Long id,
                                    @AuthenticationPrincipal AppUserDetails currentUser,
                                    Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            eventService.assertCanModify(id, userId, isAdmin);

            EventDTO event = eventService.getEventById(id);
            boolean willBeArchived = eventService.isEventInPast(id);
            model.addAttribute("event", event);
            model.addAttribute("willBeArchived", willBeArchived);
            return "events/delete";

        } catch (UnauthorizedException e) {
            return "redirect:/events?error=unauthorized";
        } catch (EventNotFoundException e) {
            return "redirect:/events?error=notfound";
        }
    }

    // DELETE SUBMIT  POST /events/{id}/delete
    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id,
                              @AuthenticationPrincipal AppUserDetails currentUser,
                              RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            boolean wasArchived = eventService.deleteOrArchiveEvent(id, userId, isAdmin);

            redirectAttributes.addFlashAttribute("successMessage",
                    wasArchived
                            ? "The event has been archived and is no longer visible to guests."
                            : "The event has been successfully deleted.");
            return "redirect:/events";

        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You are not authorized to delete this event.");
            return "redirect:/events";
        } catch (EventNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while deleting the event. Please try again.");
            return "redirect:/events";
        }
    }

    // EDIT FORM  GET /events/{id}/edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal AppUserDetails currentUser,
                               Model model) {
        try {
            boolean isAdmin = currentUser != null && currentUser.isAdmin();
            Long userId = currentUser != null ? currentUser.getId() : null;
            eventService.assertCanModify(id, userId, isAdmin);

            EventDTO event = eventService.getEventById(id);

            UpdateEventWebRequest formRequest = new UpdateEventWebRequest();
            formRequest.setName(event.getName());
            formRequest.setDescription(event.getDescription());
            formRequest.setStartDate(event.getStartDate());
            formRequest.setEndDate(event.getEndDate());
            formRequest.setDurationMinutes(event.getDurationMinutes());
            formRequest.setPrice(event.getPrice());
            formRequest.setActive(event.isActive());
            formRequest.setMenuId(event.getMenuId());

            model.addAttribute("event", event);
            model.addAttribute("formRequest", formRequest);
            model.addAttribute("menus", menuService.getAllMenus());
            return "events/edit";

        } catch (UnauthorizedException e) {
            return "redirect:/events?error=unauthorized";
        } catch (EventNotFoundException e) {
            return "redirect:/events?error=notfound";
        }
    }

    // EDIT SUBMIT  POST /events/{id}/edit
    @PostMapping("/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("formRequest") UpdateEventWebRequest formRequest,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal AppUserDetails currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        Long userId = currentUser != null ? currentUser.getId() : null;

        if (bindingResult.hasErrors()) {
            try {
                model.addAttribute("event", eventService.getEventById(id));
                model.addAttribute("menus", menuService.getAllMenus());
            } catch (EventNotFoundException e) {
                return "redirect:/events?error=notfound";
            }
            return "events/edit";
        }

        try {
            UpdateEventRequest req = new UpdateEventRequest();
            req.setName(formRequest.getName());
            req.setDescription(formRequest.getDescription());
            req.setStartDate(formRequest.getStartDate());
            req.setEndDate(formRequest.getEndDate());
            req.setDurationMinutes(formRequest.getDurationMinutes());
            req.setPrice(formRequest.getPrice());
            req.setActive(formRequest.isActive());
            req.setMenuId(formRequest.getMenuId());

            eventService.updateEvent(id, req, userId, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
            return "redirect:/events/" + id;

        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You are not authorized to edit this event.");
            return "redirect:/events";
        } catch (EventNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            try {
                model.addAttribute("event", eventService.getEventById(id));
                model.addAttribute("menus", menuService.getAllMenus());
            } catch (EventNotFoundException notFound) {
                return "redirect:/events?error=notfound";
            }
            return "events/edit";
        }
    }
}