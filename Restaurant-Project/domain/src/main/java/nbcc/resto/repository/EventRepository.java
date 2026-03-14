package nbcc.resto.repository;

import nbcc.resto.entity.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(Long id);
    Optional<Event> findByName(String name);
    List<Event> findAllActive();
    boolean existsByName(String name);

    // US5 - Delete/Archive
    void deleteById(Long id);

    // US6 - Update: check name uniqueness excluding the current event
    boolean existsByNameAndIdNot(String name, Long id);
}