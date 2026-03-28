package nbcc.resto.persistence;

import nbcc.resto.entity.Menu;
import nbcc.resto.repository.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MenuRepositoryAdapter implements MenuRepository {

    private final MenuJpaRepository jpaRepository;

    public MenuRepositoryAdapter(MenuJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Menu save(Menu menu) {
        MenuJpaEntity entity = MenuJpaEntity.fromDomain(menu);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return jpaRepository.findById(id).map(MenuJpaEntity::toDomain);
    }

    @Override
    public List<Menu> findAll() {
        return jpaRepository.findAllByOrderByCreatedDateTimeDesc()
                .stream()
                .map(MenuJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    // US3 - Update & Delete
    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return jpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}