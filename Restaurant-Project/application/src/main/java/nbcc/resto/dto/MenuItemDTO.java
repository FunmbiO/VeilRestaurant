package nbcc.resto.dto;

import nbcc.resto.entity.MenuItem;

public class MenuItemDTO {

    private Long id;
    private Long menuId;
    private String name;
    private String description;

    public static MenuItemDTO from(MenuItem item) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.id          = item.getId();
        dto.menuId      = item.getMenuId();
        dto.name        = item.getName();
        dto.description = item.getDescription();
        return dto;
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