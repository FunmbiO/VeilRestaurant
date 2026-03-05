package nbcc.resto.service;

import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.entity.Event;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventDTO createEvent(CreateEventRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidEventException("Event name is required.");
        }
        if (request.getStartDate() == null) {
            throw new InvalidEventException("Start date is required.");
        }
        if (request.getEndDate() == null) {
            throw new InvalidEventException("End date is required.");
        }
        if (request.getPrice() == null) {
            throw new InvalidEventException("Price is required.");
        }
        if (eventRepository.existsByName(request.getName())) {
            throw new DuplicateEventNameException(request.getName());
        }

        Event event = new Event(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDurationMinutes(),
                request.getPrice(),
                request.isActive()
        );

        if (!event.isDateRangeValid()) {
            throw new InvalidEventException("End date must be on or after start date.");
        }
        if (!event.isPositiveValues()) {
            throw new InvalidEventException("Price and duration must be positive values.");
        }

        event.setCreatedDate(LocalDateTime.now());
        event.setUpdatedDate(LocalDateTime.now());
        return EventDTO.from(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getAllActiveEvents() {
        return eventRepository.findAllActive()
                .stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return EventDTO.from(event);
    }
}
