package nbcc.resto;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.entity.UserLoginEntity;
import nbcc.auth.repository.UserLoginJPARepository;
import nbcc.auth.service.UserService;
import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.CreateMenuItemRequest;
import nbcc.resto.dto.CreateMenuRequest;
import nbcc.resto.dto.MenuDTO;
import nbcc.resto.service.EventService;
import nbcc.resto.service.MenuItemService;
import nbcc.resto.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final EventService eventService;
    private final UserLoginJPARepository userLoginJPARepository;

    public DataInitializer(UserService userService,
                           MenuService menuService,
                           MenuItemService menuItemService,
                           EventService eventService,
                           UserLoginJPARepository userLoginJPARepository) {
        this.userService              = userService;
        this.menuService              = menuService;
        this.menuItemService          = menuItemService;
        this.eventService             = eventService;
        this.userLoginJPARepository   = userLoginJPARepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (userService.get("admin").hasValue()) {
            log.info("DataInitializer — data already exists, skipping seed.");
            return;
        }

        log.info("DataInitializer — seeding sample data...");

        // Register admin user
        userService.register(new UserRegistration("admin", "Password1!", "admin@veil.restaurant"));

        // Get admin's ID and set isAdmin = true
        UserLoginEntity adminEntity = userLoginJPARepository
                .findByUsernameIgnoreCase("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found after seeding"));
        adminEntity.setAdmin(true);
        userLoginJPARepository.save(adminEntity);
        Long adminId = adminEntity.getId();

        // Menus

        MenuDTO springMenu = menuService.createMenu(menuRequest(
                        "Spring Awakening",
                        "A celebration of the season's first harvest — light, bright, and botanical."),
                adminId);

        MenuDTO fireMenu = menuService.createMenu(menuRequest(
                        "Ember & Smoke",
                        "A primal exploration of fire — char, smoke, and caramelised intensity."),
                adminId);

        MenuDTO oceansMenu = menuService.createMenu(menuRequest(
                        "Oceans Deep",
                        "A journey through the sea — from briny and raw to rich and roasted."),
                adminId);

        // Menu Items — Spring Awakening

        addItem(springMenu.getId(), "Pea & Mint Velouté",
                "Chilled soup of garden peas, fresh mint oil, and crème fraîche");
        addItem(springMenu.getId(), "Asparagus Tartine",
                "Grilled sourdough, whipped ricotta, shaved asparagus, lemon zest");
        addItem(springMenu.getId(), "Herb-Crusted Lamb Rack",
                "Spring lamb with parsley and pistachio crust, served with pea purée");
        addItem(springMenu.getId(), "Elderflower Panna Cotta",
                "Set cream infused with elderflower, topped with pickled strawberries");

        // Menu Items — Ember & Smoke

        addItem(fireMenu.getId(), "Charred Leek Soup",
                "Blackened leeks blended with smoked butter and croutons");
        addItem(fireMenu.getId(), "Wood-Fired Flatbread",
                "Blistered dough with smoked labneh, za'atar, and pomegranate");
        addItem(fireMenu.getId(), "Ember-Roasted Short Rib",
                "48-hour braised beef rib finished over open flame, chimichurri");
        addItem(fireMenu.getId(), "Burnt Caramel Tart",
                "Dark caramel in a smoked butter shell, sea salt, vanilla cream");

        // Menu Items — Oceans Deep

        addItem(oceansMenu.getId(), "Oyster Mignonette",
                "Three freshly shucked oysters with shallot vinegar and micro herbs");
        addItem(oceansMenu.getId(), "Seared Scallops",
                "Scallops with cauliflower purée, crispy capers, and brown butter");
        addItem(oceansMenu.getId(), "Butter-Poached Lobster Tail",
                "Nova Scotia lobster, saffron beurre blanc, sea vegetables");
        addItem(oceansMenu.getId(), "Salted Dark Chocolate Mousse",
                "Light chocolate mousse with fleur de sel and candied kelp");

        // Events

        eventService.createEvent(eventRequest(
                "Spring Awakening — An Intimate Evening",
                "An intimate evening celebrating the season's first produce in a hidden garden venue.",
                LocalDateTime.of(2026, 5, 10, 19, 0),
                LocalDateTime.of(2026, 5, 10, 22, 30),
                210, new BigDecimal("285.00"), springMenu.getId()), adminId);

        eventService.createEvent(eventRequest(
                "Ember & Obsidian — A Fire Menu",
                "An exploration of fire as a culinary element. Expect char, smoke, and bold flavours.",
                LocalDateTime.of(2026, 5, 24, 19, 30),
                LocalDateTime.of(2026, 5, 24, 23, 0),
                210, new BigDecimal("320.00"), fireMenu.getId()), adminId);

        eventService.createEvent(eventRequest(
                "Oceans Deep — A Seafood Journey",
                "A five-course journey through the sea, from the raw bar to the deep ocean floor.",
                LocalDateTime.of(2026, 6, 7, 18, 30),
                LocalDateTime.of(2026, 6, 7, 22, 0),
                210, new BigDecimal("350.00"), oceansMenu.getId()), adminId);

        log.info("DataInitializer — seeding complete: 1 admin user, 3 menus, 12 menu items, 3 events.");
    }

    private CreateMenuRequest menuRequest(String name, String description) {
        CreateMenuRequest req = new CreateMenuRequest();
        req.setName(name);
        req.setDescription(description);
        return req;
    }

    private void addItem(Long menuId, String name, String description) {
        CreateMenuItemRequest req = new CreateMenuItemRequest();
        req.setName(name);
        req.setDescription(description);
        menuItemService.createMenuItem(menuId, req);
    }

    private CreateEventRequest eventRequest(String name, String description,
                                            LocalDateTime start, LocalDateTime end,
                                            int duration, BigDecimal price, Long menuId) {
        CreateEventRequest req = new CreateEventRequest();
        req.setName(name);
        req.setDescription(description);
        req.setStartDate(start);
        req.setEndDate(end);
        req.setDurationMinutes(duration);
        req.setPrice(price);
        req.setActive(true);
        req.setMenuId(menuId);
        return req;
    }
}