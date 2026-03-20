package nbcc.resto.service;

import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.entity.Menu;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
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
}