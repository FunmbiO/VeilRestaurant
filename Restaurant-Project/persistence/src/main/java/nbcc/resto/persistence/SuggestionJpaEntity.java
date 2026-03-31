package nbcc.resto.persistence;

import jakarta.persistence.*;
import nbcc.resto.entity.Suggestion;
import nbcc.resto.entity.Suggestion.Priority;
import nbcc.resto.entity.Suggestion.Status;
import nbcc.resto.entity.Suggestion.TargetType;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggestions")
public class SuggestionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String suggestionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;

    @Column(nullable = true)
    private Long createdBy;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = true)
    private Long fulfilledBy;

    @Column(nullable = true)
    private LocalDateTime fulfilledDate;

    public SuggestionJpaEntity() {}

    public static SuggestionJpaEntity fromDomain(Suggestion s) {
        SuggestionJpaEntity e = new SuggestionJpaEntity();
        e.id             = s.getId();
        e.targetType     = s.getTargetType();
        e.targetId       = s.getTargetId();
        e.suggestionText = s.getSuggestionText();
        e.priority       = s.getPriority();
        e.status         = s.getStatus();
        e.createdBy      = s.getCreatedBy();
        e.createdDate    = s.getCreatedDate();
        e.isRead         = s.isRead();
        e.fulfilledBy    = s.getFulfilledBy();
        e.fulfilledDate  = s.getFulfilledDate();
        return e;
    }

    public Suggestion toDomain() {
        Suggestion s = new Suggestion();
        s.setId(id);
        s.setTargetType(targetType);
        s.setTargetId(targetId);
        s.setSuggestionText(suggestionText);
        s.setPriority(priority);
        s.setStatus(status);
        s.setCreatedBy(createdBy);
        s.setCreatedDate(createdDate);
        s.setRead(isRead);
        s.setFulfilledBy(fulfilledBy);
        s.setFulfilledDate(fulfilledDate);
        return s;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getSuggestionText() { return suggestionText; }
    public void setSuggestionText(String suggestionText) { this.suggestionText = suggestionText; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public Long getFulfilledBy() { return fulfilledBy; }
    public void setFulfilledBy(Long fulfilledBy) { this.fulfilledBy = fulfilledBy; }
    public LocalDateTime getFulfilledDate() { return fulfilledDate; }
    public void setFulfilledDate(LocalDateTime fulfilledDate) { this.fulfilledDate = fulfilledDate; }
}