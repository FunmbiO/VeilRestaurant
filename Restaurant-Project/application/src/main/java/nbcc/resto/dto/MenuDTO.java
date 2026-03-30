package nbcc.resto.dto;

import nbcc.resto.entity.Menu;

import java.time.LocalDateTime;

public class MenuDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDateTime;
    private Long createdBy;
    private String createdByUsername;

    public static MenuDTO from(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.id              = menu.getId();
        dto.name            = menu.getName();
        dto.description     = menu.getDescription();
        dto.createdDateTime = menu.getCreatedDateTime();
        dto.createdBy = menu.getCreatedBy();
        dto.createdByUsername = menu.getCreatedByUsername();
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
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
}