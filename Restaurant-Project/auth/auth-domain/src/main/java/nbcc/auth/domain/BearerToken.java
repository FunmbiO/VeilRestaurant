package nbcc.auth.domain;

import java.security.Principal;

public class BearerToken implements Principal {

    private String userName;
    private long expiresIn;
    private String token;
    private String tokenType;
    private JWTClaims claims;

    public BearerToken() {
    }

    public BearerToken(String token, String userName, long expiresIn, JWTClaims claims) {
        this(token, userName, expiresIn, "Bearer", claims);
    }

    public BearerToken(String token, String userName, long expiresIn, String tokenType, JWTClaims claims) {
        this();
        this.userName = userName;
        this.expiresIn = expiresIn;
        this.token = token;
        this.tokenType = tokenType;
        this.claims = claims;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public JWTClaims getClaims() {
        return claims;
    }

    public void setClaims(JWTClaims claims) {
        this.claims = claims;
    }

    @Override
    public String toString() {
        return token;
    }

    @Override
    public String getName() {
        return getUserName();
    }
}
