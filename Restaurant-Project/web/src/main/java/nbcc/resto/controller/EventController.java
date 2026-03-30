package nbcc.resto.controller;

import jakarta.validation.Valid;
import nbcc.auth.security.AppUserDetails;
import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.request.CreateEventWebRequest;
import nbcc.resto.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventWebRequest webRequest,
                                                @AuthenticationPrincipal AppUserDetails currentUser) {
        CreateEventRequest req = new CreateEventRequest();
        req.setName(webRequest.getName());
        req.setDescription(webRequest.getDescription());
        req.setStartDate(webRequest.getStartDate());
        req.setEndDate(webRequest.getEndDate());
        req.setDurationMinutes(webRequest.getDurationMinutes());
        req.setPrice(webRequest.getPrice());
        req.setActive(webRequest.isActive());
        Long createdBy = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(req, createdBy));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> listEvents() {
        return ResponseEntity.ok(eventService.getAllActiveEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }
}
