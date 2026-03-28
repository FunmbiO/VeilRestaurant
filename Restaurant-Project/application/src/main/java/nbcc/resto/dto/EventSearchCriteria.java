package nbcc.resto.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

/**
 * Carries search criteria from the UI → Controller → Service → Repository.
 */
public class EventSearchCriteria {

    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    /**
     * ALL     – no date filtering (default)
     * AFTER   – startDate is after the given startDate
     * BEFORE  – startDate is before the given startDate
     * BETWEEN – startDate is between startDate and endDate
     */
    private String dateRangeFilter = "ALL";

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getDateRangeFilter() { return dateRangeFilter; }
    public void setDateRangeFilter(String dateRangeFilter) { this.dateRangeFilter = dateRangeFilter; }
}