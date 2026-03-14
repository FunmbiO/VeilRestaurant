package nbcc.resto.persistence;

import nbcc.resto.entity.Event;
import nbcc.resto.repository.EventRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventRepositoryAdapter implements EventRepository {

    private final EventJpaRepository jpaRepository;
    private final EventMapper mapper;

    public EventRepositoryAdapter(EventJpaRepository jpaRepository, EventMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Event save(Event event) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(event)));
    }

    @Override
    public Optional<Event> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Event> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Event> findAllActive() {
        return jpaRepository.findByActiveTrueOrderByStartDateAsc()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return false;
    }
}