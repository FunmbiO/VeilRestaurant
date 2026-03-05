package nbcc.auth.entity;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;


@Entity
@Table(name = "user")
public class UserLoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean enabled;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean locked;

    public UserLoginEntity() {
        enabled = true;
        locked = false;
    }

    public UserLoginEntity(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public UserLoginEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserLoginEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserLoginEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserLoginEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserLoginEntity setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public UserLoginEntity setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }
}

