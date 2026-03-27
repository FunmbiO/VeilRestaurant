package nbcc.resto.entity;

public class MenuItem {

    private Long id;
    private Long menuId;
    private String name;
    private String description;

    public MenuItem() {}

    public MenuItem(Long menuId, String name, String description) {
        this.menuId      = menuId;
        this.name        = name;
        this.description = description;
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
