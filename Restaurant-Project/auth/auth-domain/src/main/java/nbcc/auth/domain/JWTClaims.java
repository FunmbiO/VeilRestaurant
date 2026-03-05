package nbcc.auth.domain;

import java.util.HashMap;
import java.util.Map;

public class JWTClaims extends HashMap<String, Object> {

    public JWTClaims() {}

    public JWTClaims(Map<String, Object> claims) {
        super(claims);
    }

    public static final String ID = "id";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    public Long getId() {
        var claimsObject = get(ID);
        try{
            return Long.parseLong(String.valueOf(claimsObject));
        }catch (NumberFormatException e){
            return null;
        }
    }

    public JWTClaims setId(Long id) {
        put(ID, id);
        return this;
    }

    public String getUserName() {
        var claimsObject = get(USERNAME);
        return String.valueOf(claimsObject);
    }

    public JWTClaims setUserName(String userName) {
        put(USERNAME, userName);
        return this;
    }

    public String getEmail() {
        var claimsObject = get(EMAIL);
        return String.valueOf(claimsObject);
    }

    public JWTClaims setEmail(String email) {
        put(EMAIL, email);
        return this;
    }

}
