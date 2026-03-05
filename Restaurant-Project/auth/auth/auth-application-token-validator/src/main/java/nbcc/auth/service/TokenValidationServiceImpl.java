package nbcc.auth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import nbcc.auth.domain.JWTClaims;
import nbcc.auth.exception.AuthException;
import nbcc.auth.exception.TokenException;
import nbcc.auth.exception.UnauthorizedException;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

@Service
public class TokenValidationServiceImpl implements TokenValidationService {

    private final Logger logger = LoggerFactory.getLogger(TokenValidationServiceImpl.class);

    private final JWSVerifier verifier;

    public TokenValidationServiceImpl(RSAPublicKey publicKey) {
        this.verifier = new RSASSAVerifier(publicKey);
    }

    @Override
    public AuthResult<JWTClaims> validateToken(String token)  {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify signature
            if (!signedJWT.verify(verifier)) {
                throw new TokenException("Invalid token signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Check expiration
            if (claims.getExpirationTime().before(new Date())) {
                throw new UnauthorizedException("Token expired");
            }

            // Convert to your JWTClaims
            var jwtClaims = new JWTClaims(claims.getClaims());
            return AuthResult.success(jwtClaims);

        } catch (JOSEException | ParseException | AuthException e) {
            logger.info("Token validation failed", e);
        } catch (Exception e) {
            logger.error("Unexpected error during token validation", e);
        }

        return AuthResult.error();
    }
}
