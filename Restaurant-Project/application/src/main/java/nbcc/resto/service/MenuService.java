package nbcc.resto.service;

import jakarta.validation.Valid;
import nbcc.auth.entity.UserLoginEntity;
import nbcc.auth.repository.UserLoginJPARepository;
import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.dto.UpdateMenuRequest;
import nbcc.resto.entity.Menu;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.exception.UnauthorizedException;
import nbcc.resto.repository.EventRepository;
import nbcc.resto.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final EventRepository eventRepository;
    private final UserLoginJPARepository userLoginJPARepository;

    public MenuService(MenuRepository menuRepository,
                       EventRepository eventRepository,
                       UserLoginJPARepository userLoginJPARepository) {
        this.menuRepository         = menuRepository;
        this.eventRepository        = eventRepository;
        this.userLoginJPARepository = userLoginJPARepository;
    }

    // Helpers

    private String resolveUsername(Long userId) {
        if (userId == null) return null;
        return userLoginJPARepository.findById(userId)
                .map(UserLoginEntity::getUsername)
                .orElse("[Deleted User]");
    }

    private MenuDTO toDTO(Menu menu) {
        MenuDTO dto = MenuDTO.from(menu);
        dto.setCreatedByUsername(resolveUsername(menu.getCreatedBy()));
        return dto;
    }

    // Authorization

    public void assertCanModify(Long menuId, Long currentUserId, boolean isAdmin) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));
        if (!isAdmin && !Objects.equals(menu.getCreatedBy(), currentUserId)) {
            throw new UnauthorizedException("You do not have permission to modify this menu.");
        }
    }

    public boolean canModify(Long menuId, Long currentUserId, boolean isAdmin) {
        if (isAdmin) return true;
        return menuRepository.findById(menuId)
                .map(m -> Objects.equals(m.getCreatedBy(), currentUserId))
                .orElse(false);
    }

    // Create

    @Transactional
    public MenuDTO createMenu(CreateMenuRequest request, Long createdBy) {
        if (request.getName() == null || request.getName().isBlank())
            throw new InvalidEventException("Menu name is required.");
        if (menuRepository.existsByName(request.getName().trim()))
            throw new DuplicateEventNameException(request.getName().trim());

        Menu menu = new Menu(request.getName().trim(), request.getDescription());
        menu.setCreatedDateTime(LocalDateTime.now());
        menu.setCreatedBy(createdBy);

        return toDTO(menuRepository.save(menu));
    }

    // Read

    @Transactional(readOnly = true)
    public List<MenuDTO> getAllMenus() {
        return menuRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuDTO getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        return toDTO(menu);
    }

    // Update

    @Transactional
    public MenuDTO updateMenu(Long id, @Valid UpdateMenuRequest request,
                              Long currentUserId, boolean isAdmin) {
        assertCanModify(id, currentUserId, isAdmin);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        if (request.getName() == null || request.getName().isBlank())
            throw new InvalidEventException("Menu name is required.");
        if (menuRepository.existsByNameAndIdNot(request.getName().trim(), id))
            throw new DuplicateEventNameException(request.getName().trim());

        menu.setName(request.getName().trim());
        menu.setDescription(request.getDescription());
        return toDTO(menuRepository.save(menu));
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
    public void deleteMenu(Long id, Long currentUserId, boolean isAdmin) {
        assertCanModify(id, currentUserId, isAdmin);
        menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        menuRepository.deleteById(id);
    }

    // Search

    @Transactional(readOnly = true)
    public List<MenuDTO> searchMenus(String name) {
        if (name == null || name.isBlank())
            return getAllMenus();
        return menuRepository.searchByName(name.trim())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}