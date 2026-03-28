package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.dto.UpdateEventRequest;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.request.UpdateEventWebRequest;
import nbcc.resto.service.EventService;
import nbcc.resto.service.MenuService;
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
        this.menuService = menuService;
    }
    // US5 - Delete / Archive Event

    @GetMapping("/{id}/delete")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        try {
            EventDTO event = eventService.getEventById(id);
            boolean willBeArchived = eventService.isEventInPast(id);
            model.addAttribute("event", event);
            model.addAttribute("willBeArchived", willBeArchived);
            return "events/delete";
        } catch (EventNotFoundException e) {
            return "redirect:/events?error=notfound";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // deleteOrArchiveEvent returns true if archived, false if deleted
            boolean wasArchived = eventService.deleteOrArchiveEvent(id);

            if (wasArchived) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "The event has been archived and is no longer visible to guests.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "The event has been successfully deleted.");
            }
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

    // US6 - Update / Edit Event

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            EventDTO event = eventService.getEventById(id);

            UpdateEventWebRequest formRequest = new UpdateEventWebRequest();
            formRequest.setName(event.getName());
            formRequest.setDescription(event.getDescription());
            formRequest.setStartDate(event.getStartDate());
            formRequest.setEndDate(event.getEndDate());
            formRequest.setDurationMinutes(event.getDurationMinutes());
            formRequest.setPrice(event.getPrice());
            formRequest.setActive(event.isActive());
            formRequest.setMenuId(event.getMenuId());   // add this

            model.addAttribute("event", event);
            model.addAttribute("formRequest", formRequest);
            model.addAttribute("menus", menuService.getAllMenus());  // add this
            return "events/edit";

        } catch (EventNotFoundException e) {
            return "redirect:/events?error=notfound";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("formRequest") UpdateEventWebRequest formRequest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            try {
                EventDTO event = eventService.getEventById(id);
                model.addAttribute("event", event);
                model.addAttribute("menus", menuService.getAllMenus());  // add this
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
            req.setMenuId(formRequest.getMenuId());   // add this

            eventService.updateEvent(id, req);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
            return "redirect:/events/" + id;

        } catch (EventNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            try {
                EventDTO event = eventService.getEventById(id);
                model.addAttribute("event", event);
                model.addAttribute("menus", menuService.getAllMenus());  // add this
            } catch (EventNotFoundException notFound) {
                return "redirect:/events?error=notfound";
            }
            return "events/edit";
        }
    }
}