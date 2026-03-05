package nbcc.auth.config;

public interface AuthClientConfig {

    String getBaseUrl();

    String getPort();

    default String getBaseAddress() {
        return getBaseUrl() + ":" + getPort();
    }
}
