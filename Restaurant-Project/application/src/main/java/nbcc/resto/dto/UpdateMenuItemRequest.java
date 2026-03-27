package nbcc.resto.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateMenuItemRequest {

    @NotBlank(message = "Item name is required.")
    private String name;

    @NotBlank(message = "Item description is required.")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}