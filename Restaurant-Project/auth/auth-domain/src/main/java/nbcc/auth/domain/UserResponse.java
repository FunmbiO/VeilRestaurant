package nbcc.auth.domain;

import java.security.Principal;

public class UserResponse implements Principal {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private boolean locked;

    public UserResponse() {
        locked = false;
    }

    public UserResponse(UserRegistration userRegistration) {
        this();
        this.username = userRegistration.getUsername();
        this.email = userRegistration.getEmail();
        this.enabled = userRegistration.isEnabled();
    }

    public Long getId() {
        return id;
    }

    public UserResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserResponse setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public UserResponse setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
