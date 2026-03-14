package nbcc.resto.service;

import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.dto.UpdateEventRequest;
import nbcc.resto.entity.Event;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.repository.EventRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(@Qualifier("eventRepositoryImpl") EventRepository eventRepository) {
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

    // -------------------------------------------------------------------------
    // US5 - Delete / Archive Event
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public boolean isEventInPast(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return event.getEndDate().isBefore(LocalDateTime.now());
    }

    @Transactional
    public boolean deleteOrArchiveEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        boolean isPast = event.getEndDate().isBefore(LocalDateTime.now());

        if (isPast) {
            event.setActive(false);
            event.setUpdatedDate(LocalDateTime.now());
            eventRepository.save(event);
        } else {
            eventRepository.deleteById(id);
        }

        return isPast;
    }

    // -------------------------------------------------------------------------
    // US6 - Update / Edit Event
    // -------------------------------------------------------------------------

    @Transactional
    public EventDTO updateEvent(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

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

        if (eventRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateEventNameException(request.getName());
        }

        Event temp = new Event(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDurationMinutes(),
                request.getPrice(),
                request.isActive()
        );
        if (!temp.isDateRangeValid()) {
            throw new InvalidEventException("End date must be on or after start date.");
        }
        if (!temp.isPositiveValues()) {
            throw new InvalidEventException("Price and duration must be positive values.");
        }

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setDurationMinutes(request.getDurationMinutes());
        event.setPrice(request.getPrice());
        event.setActive(request.isActive());
        event.setUpdatedDate(LocalDateTime.now());

        return EventDTO.from(eventRepository.save(event));
    }
}