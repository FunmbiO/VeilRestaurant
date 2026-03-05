package nbcc.auth.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import nbcc.auth.config.JWTConfig;
import nbcc.auth.domain.BearerToken;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenCreationServiceImpl implements TokenCreationService {

    private final Logger logger = LoggerFactory.getLogger(TokenCreationServiceImpl.class);

    private final RSASSASigner signer;
    private final JWTConfig jwtConfig;

    public TokenCreationServiceImpl(RSAPrivateKey privateKey, JWTConfig jwtConfig) {
        this.signer = new RSASSASigner(privateKey);
        this.jwtConfig = jwtConfig;
    }

    public AuthResult<BearerToken> createToken(UserResponse user) {
        try {
            Instant now = Instant.now();
            Date startDate = Date.from(now);
            Date expiryDate = Date.from(now.plusSeconds(jwtConfig.getTimeout()));

            JWTClaims claims = getClaims(user);

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer(jwtConfig.getIssuer())
                    .issueTime(startDate)
                    .expirationTime(expiryDate);

            for (var entry : claims.entrySet()) {
                claimsBuilder.claim(entry.getKey(), entry.getValue());
            }

            // Include keyId in the header so validators know which key to use
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    //.keyID(keyId)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsBuilder.build());
            signedJWT.sign(signer);

            var token = new BearerToken(signedJWT.serialize(), user.getUsername(), jwtConfig.getTimeout(), claims);
            return AuthResult.success(token, true);
        } catch (Exception e) {
           logger.error("Error creating token for user: {}", user.getUsername(), e);
            return AuthResult.error(e);
        }
    }

    private JWTClaims getClaims(UserResponse user) {
        return new JWTClaims()
                .setId(user.getId())
                .setUserName(user.getUsername())
                .setEmail(user.getEmail());
    }
}