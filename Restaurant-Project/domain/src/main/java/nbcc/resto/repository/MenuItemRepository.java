package nbcc.resto.repository;

import nbcc.resto.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    MenuItem save(MenuItem item);
    List<MenuItem> findByMenuId(Long menuId);

    // US5 - Update & Delete
    Optional<MenuItem> findById(Long id);
    void deleteById(Long id);
}