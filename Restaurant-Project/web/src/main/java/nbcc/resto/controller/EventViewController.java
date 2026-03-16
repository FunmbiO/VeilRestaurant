package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * MVC controller for Thymeleaf views.
 * Separate from EventController (REST) so both can coexist cleanly.
 */
@Controller
public class EventViewController {

    private final EventService eventService;

    public EventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    // LIST EVENTS  GET /events
    @GetMapping("/events")
    public String listEvents(Model model) {
        List<EventDTO> events = eventService.getAllActiveEvents();
        model.addAttribute("events", events);
        return "events/list";
    }

    // EVENT DETAIL  GET /events/{id}
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        try {
            EventDTO event = eventService.getEventById(id);
            model.addAttribute("event", event);
            return "events/detail";
        } catch (EventNotFoundException e) {
            return "redirect:/events";
        }
    }
    // CREATE EVENT FORM  GET /events/new
    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createEventRequest", new CreateEventRequest());
        return "events/create";
    }
    // CREATE EVENT SUBMIT  POST /events/new
    @PostMapping("/events/new")
    public String submitCreateForm(
            @Valid @ModelAttribute CreateEventRequest createEventRequest,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "events/create";
        }

        try {
            EventDTO created = eventService.createEvent(createEventRequest);
            return "redirect:/events/" + created.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "events/create";
        }
    }
}