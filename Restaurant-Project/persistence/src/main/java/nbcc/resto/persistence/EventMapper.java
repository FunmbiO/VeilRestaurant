package nbcc.resto.persistence;

import nbcc.resto.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventJpaEntity toJpa(Event event) {
        EventJpaEntity jpa = new EventJpaEntity();
        jpa.setId(event.getId());
        jpa.setName(event.getName());
        jpa.setDescription(event.getDescription());
        jpa.setStartDate(event.getStartDate());
        jpa.setEndDate(event.getEndDate());
        jpa.setDurationMinutes(event.getDurationMinutes());
        jpa.setPrice(event.getPrice());
        jpa.setActive(event.isActive());
        jpa.setCreatedDate(event.getCreatedDate());
        jpa.setUpdatedDate(event.getUpdatedDate());
        return jpa;
    }

    public Event toDomain(EventJpaEntity jpa) {
        Event event = new Event();
        event.setId(jpa.getId());
        event.setName(jpa.getName());
        event.setDescription(jpa.getDescription());
        event.setStartDate(jpa.getStartDate());
        event.setEndDate(jpa.getEndDate());
        event.setDurationMinutes(jpa.getDurationMinutes());
        event.setPrice(jpa.getPrice());
        event.setActive(jpa.isActive());
        event.setCreatedDate(jpa.getCreatedDate());
        event.setUpdatedDate(jpa.getUpdatedDate());
        return event;
    }
}
