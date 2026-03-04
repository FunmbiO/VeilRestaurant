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
