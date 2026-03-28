package nbcc.resto.entity;

import java.time.LocalDateTime;

public class Menu {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDateTime;

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
}
