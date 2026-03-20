package nbcc.resto.repository;

import nbcc.resto.entity.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    Menu save(Menu menu);
    Optional<Menu> findById(Long id);
    List<Menu> findAll();
    boolean existsByName(String name);
}