package nbcc.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class PrivateKeyConfig {

    @Bean
    public RSAPrivateKey rsaPrivateKey(
            @Value("${jwt.private-key-location}") Resource resource) throws Exception {
        String pem = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "\n")  // Convert literal \n to actual newlines
                .replaceAll(System.lineSeparator(), "")  // Remove all newline characters
                .replaceAll("\\s", "");  // Remove all whitespace

        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}
