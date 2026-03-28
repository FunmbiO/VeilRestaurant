package nbcc.resto.persistence;

import jakarta.persistence.*;
import nbcc.resto.entity.MenuItem;

@Entity
@Table(name = "menu_items")
public class MenuItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public MenuItemJpaEntity() {}

    public static MenuItemJpaEntity fromDomain(MenuItem item) {
        MenuItemJpaEntity e = new MenuItemJpaEntity();
        e.id          = item.getId();
        e.menuId      = item.getMenuId();
        e.name        = item.getName();
        e.description = item.getDescription();
        return e;
    }

    public MenuItem toDomain() {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setMenuId(menuId);
        item.setName(name);
        item.setDescription(description);
        return item;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
