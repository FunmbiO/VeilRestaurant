package nbcc.resto.persistence;

import nbcc.resto.entity.Event;
import nbcc.resto.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository jpaRepository;

    public EventRepositoryImpl(EventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Event save(Event event) {
        EventJpaEntity entity = EventJpaEntity.fromDomain(event);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Event> findById(Long id) {
        return jpaRepository.findById(id).map(EventJpaEntity::toDomain);
    }

    @Override
    public Optional<Event> findByName(String name) {
        return jpaRepository.findByName(name).map(EventJpaEntity::toDomain);
    }

    @Override
    public List<Event> findAllActive() {
        return jpaRepository.findByActiveTrueOrderByStartDateAsc()
                .stream()
                .map(EventJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    // US5 - Delete
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // US6 - Update
    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return jpaRepository.existsByNameAndIdNot(name, id);
    }
}
