package nbcc.auth.domain;

public class UserRequest {

    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean locked;

    public UserRequest() {
    }

    public Long getId() {
        return id;
    }

    public UserRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserRequest setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public UserRequest setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public boolean isExpired(){
        return isLocked();
    }

    public boolean isCredentialsExpired(){
        return isLocked();
    }
}

