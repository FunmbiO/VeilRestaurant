package nbcc.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class PublicKeyConfig {

    @Bean
    public RSAPublicKey rsaPublicKey(
            @Value("${jwt.public-key-location}") Resource resource) throws Exception {
        String pem = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----","")
                .replace("\\n", "\n")  // Convert literal \n to actual newlines
                .replaceAll(System.lineSeparator(), "")  // Remove all newline characters
                .replaceAll("\\s", "");  // Remove all white

        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
