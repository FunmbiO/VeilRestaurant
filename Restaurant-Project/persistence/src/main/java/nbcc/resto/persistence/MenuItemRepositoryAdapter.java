package nbcc.resto.persistence;

import nbcc.resto.entity.MenuItem;
import nbcc.resto.repository.MenuItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuItemRepositoryAdapter implements MenuItemRepository {

    private final MenuItemJpaRepository jpaRepository;

    public MenuItemRepositoryAdapter(MenuItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MenuItem save(MenuItem item) {
        return jpaRepository.save(MenuItemJpaEntity.fromDomain(item)).toDomain();
    }

    @Override
    public List<MenuItem> findByMenuId(Long menuId) {
        return jpaRepository.findByMenuIdOrderByIdAsc(menuId)
                .stream()
                .map(MenuItemJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}