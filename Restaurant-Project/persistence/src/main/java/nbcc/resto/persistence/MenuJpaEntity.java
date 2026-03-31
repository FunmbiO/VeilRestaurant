package nbcc.resto.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
public class MenuJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;
    @Column(nullable = true)
    private Long createdBy;


    public MenuJpaEntity() {}

    // Mapping

    public static MenuJpaEntity fromDomain(nbcc.resto.entity.Menu menu) {
        MenuJpaEntity e = new MenuJpaEntity();
        e.id              = menu.getId();
        e.name            = menu.getName();
        e.description     = menu.getDescription();
        e.createdDateTime = menu.getCreatedDateTime();
        e.createdBy = menu.getCreatedBy();
        return e;
    }

    public nbcc.resto.entity.Menu toDomain() {
        nbcc.resto.entity.Menu m = new nbcc.resto.entity.Menu();
        m.setId(id);
        m.setName(name);
        m.setDescription(description);
        m.setCreatedDateTime(createdDateTime);
        m.setCreatedBy(this.createdBy);
        return m;
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
}