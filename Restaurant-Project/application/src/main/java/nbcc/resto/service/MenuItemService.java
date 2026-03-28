package nbcc.resto.service;

import nbcc.resto.dto.CreateMenuItemRequest;
import nbcc.resto.dto.MenuItemDTO;
import nbcc.resto.dto.UpdateMenuItemRequest;
import nbcc.resto.entity.MenuItem;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.exception.MenuItemNotFoundException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.repository.MenuItemRepository;
import nbcc.resto.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           MenuRepository menuRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository     = menuRepository;
    }

    // Create

    @Transactional
    public MenuItemDTO createMenuItem(Long menuId, CreateMenuItemRequest request) {
        // Verify menu exists
        menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidEventException("Item name is required.");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new InvalidEventException("Item description is required.");
        }

        MenuItem item = new MenuItem(menuId, request.getName().trim(), request.getDescription().trim());
        return MenuItemDTO.from(menuItemRepository.save(item));
    }

    // List by Menu

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getItemsByMenuId(Long menuId) {
        return menuItemRepository.findByMenuId(menuId)
                .stream()
                .map(MenuItemDTO::from)
                .collect(Collectors.toList());
    }
    // Get by ID

    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
        return MenuItemDTO.from(item);
    }

    //  Update

    @Transactional
    public MenuItemDTO updateMenuItem(Long id, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidEventException("Item name is required.");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new InvalidEventException("Item description is required.");
        }

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        return MenuItemDTO.from(menuItemRepository.save(item));
    }

    // Delete

    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
        menuItemRepository.deleteById(id);
    }
}
