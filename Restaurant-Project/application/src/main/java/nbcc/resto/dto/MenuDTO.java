package nbcc.resto.dto;

import nbcc.resto.entity.Menu;

import java.time.LocalDateTime;

public class MenuDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDateTime;

    public static MenuDTO from(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.id              = menu.getId();
        dto.name            = menu.getName();
        dto.description     = menu.getDescription();
        dto.createdDateTime = menu.getCreatedDateTime();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }
    public void setCreatedDateTime(LocalDateTime createdDateTime) { this.createdDateTime = createdDateTime; }
}