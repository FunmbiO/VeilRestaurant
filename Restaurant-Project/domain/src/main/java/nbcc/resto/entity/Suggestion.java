package nbcc.resto.entity;

import java.time.LocalDateTime;

public class Suggestion {

    public enum TargetType { EVENT, MENU }
    public enum Priority   { HIGH, NORMAL, LOW }
    public enum Status     { PENDING, FULFILLED, DISCARDED }

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
    private Long fulfilledBy;
    private LocalDateTime fulfilledDate;

    public Suggestion() {}

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
    public Long getFulfilledBy() { return fulfilledBy; }
    public void setFulfilledBy(Long fulfilledBy) { this.fulfilledBy = fulfilledBy; }
    public LocalDateTime getFulfilledDate() { return fulfilledDate; }
    public void setFulfilledDate(LocalDateTime fulfilledDate) { this.fulfilledDate = fulfilledDate; }
}