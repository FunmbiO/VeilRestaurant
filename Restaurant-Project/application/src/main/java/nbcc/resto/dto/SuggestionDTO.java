package nbcc.resto.dto;

import nbcc.resto.entity.Suggestion;
import nbcc.resto.entity.Suggestion.Priority;
import nbcc.resto.entity.Suggestion.Status;
import nbcc.resto.entity.Suggestion.TargetType;

import java.time.LocalDateTime;

public class SuggestionDTO {

    private Long id;
    private TargetType targetType;
    private Long targetId;
    private String targetName;
    private String suggestionText;
    private Priority priority;
    private Status status;
    private Long createdBy;
    private String createdByUsername;
    private LocalDateTime createdDate;
    private boolean isRead;

    public static SuggestionDTO from(Suggestion s) {
        SuggestionDTO dto = new SuggestionDTO();
        dto.id                = s.getId();
        dto.targetType        = s.getTargetType();
        dto.targetId          = s.getTargetId();
        dto.targetName        = s.getTargetName();
        dto.suggestionText    = s.getSuggestionText();
        dto.priority          = s.getPriority();
        dto.status            = s.getStatus();
        dto.createdBy         = s.getCreatedBy();
        dto.createdByUsername = s.getCreatedByUsername();
        dto.createdDate       = s.getCreatedDate();
        dto.isRead            = s.isRead();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getSuggestionText() { return suggestionText; }
    public void setSuggestionText(String suggestionText) { this.suggestionText = suggestionText; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}