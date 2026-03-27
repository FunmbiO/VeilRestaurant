package nbcc.resto.repository;

import nbcc.resto.entity.MenuItem;

import java.util.List;

public interface MenuItemRepository {
    MenuItem save(MenuItem item);
    List<MenuItem> findByMenuId(Long menuId);
}