package nbcc.resto.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Event {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationMinutes;
    private BigDecimal price;
    private boolean active;
    private Long menuId;           // US2 - nullable, links to Menu
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Event() {}

    public Event(String name, String description, LocalDateTime startDate,
                 LocalDateTime endDate, Integer durationMinutes,
                 BigDecimal price, boolean active) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.active = active;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return false;
        return !endDate.isBefore(startDate);
    }

    public boolean isPositiveValues() {
        boolean priceOk = price == null || price.compareTo(BigDecimal.ZERO) >= 0;
        boolean durationOk = durationMinutes == null || durationMinutes >= 0;
        return priceOk && durationOk;
    }

    // US2 - an event cannot be active (live) without a menu
    public boolean isLiveWithoutMenu() {
        return this.active && this.menuId == null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}