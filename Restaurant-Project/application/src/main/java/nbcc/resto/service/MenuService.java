package nbcc.resto.service;

import jakarta.validation.Valid;
import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.dto.UpdateMenuRequest;
import nbcc.resto.entity.Menu;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.repository.EventRepository;
import nbcc.resto.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final EventRepository eventRepository;

    public MenuService(MenuRepository menuRepository, EventRepository eventRepository) {
        this.menuRepository = menuRepository;
        this.eventRepository = eventRepository;
    }

    // Create

    @Transactional
    public MenuDTO createMenu(CreateMenuRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidEventException("Menu name is required.");
        }
        if (menuRepository.existsByName(request.getName().trim())) {
            throw new DuplicateEventNameException(request.getName().trim());
        }

        Menu menu = new Menu(request.getName().trim(), request.getDescription());
        menu.setCreatedDateTime(LocalDateTime.now());

        return MenuDTO.from(menuRepository.save(menu));
    }

    // List

    @Transactional(readOnly = true)
    public List<MenuDTO> getAllMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuDTO::from)
                .collect(Collectors.toList());
    }
    // Get by ID
    @Transactional(readOnly = true)
    public MenuDTO getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        return MenuDTO.from(menu);
    }

    // Update

    @Transactional
    public MenuDTO updateMenu(Long id, @Valid UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidEventException("Menu name is required.");
        }
        if (menuRepository.existsByNameAndIdNot(request.getName().trim(), id)) {
            throw new DuplicateEventNameException(request.getName().trim());
        }

        menu.setName(request.getName().trim());
        menu.setDescription(request.getDescription());
        return MenuDTO.from(menuRepository.save(menu));
    }

    // Delete

    @Transactional(readOnly = true)
    public List<String> getAssociatedEventNames(Long menuId) {
        return eventRepository.findAllActive()
                .stream()
                .filter(e -> menuId.equals(e.getMenuId()))
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMenu(Long id) {
        menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        menuRepository.deleteById(id);
    }
}