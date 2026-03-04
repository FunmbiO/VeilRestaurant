package nbcc.resto.dto;

import nbcc.resto.entity.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationMinutes;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public EventDTO() {}

    public static EventDTO from(Event event) {
        EventDTO dto = new EventDTO();
        dto.id = event.getId();
        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.startDate = event.getStartDate();
        dto.endDate = event.getEndDate();
        dto.durationMinutes = event.getDurationMinutes();
        dto.price = event.getPrice();
        dto.active = event.isActive();
        dto.createdDate = event.getCreatedDate();
        dto.updatedDate = event.getUpdatedDate();
        return dto;
    }
