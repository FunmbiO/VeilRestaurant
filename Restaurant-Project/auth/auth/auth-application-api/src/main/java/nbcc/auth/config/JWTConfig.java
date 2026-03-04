package nbcc.auth.config;

public interface JWTConfig {
    long getTimeout();
    String getIssuer();
}
