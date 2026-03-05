package nbcc.common.service;

public interface LoginService {

    boolean isLoggedIn();

    boolean isLoggedOut();

    String getCurrentUsername();

}