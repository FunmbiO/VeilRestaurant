package nbcc.resto.entity;

import java.time.LocalDateTime;

public class Menu {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDateTime;
    private Long createdBy;           // add with other fields
    private String createdByUsername; // for display only


    public Menu() {}

    public Menu(String name, String description) {
        this.name            = name;
        this.description     = description;
        this.createdDateTime = LocalDateTime.now();
    }

    // Getters & Setters

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
