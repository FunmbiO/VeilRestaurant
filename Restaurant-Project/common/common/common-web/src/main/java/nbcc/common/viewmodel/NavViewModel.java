package nbcc.common.viewmodel;

public class NavViewModel {

    private final String username;

    private final boolean showLoginLink;
    private final boolean showLogoutButton;
    private final boolean showRegisterLink;
    private final boolean showUserName;
    private final boolean showAlbums;

    public NavViewModel(boolean isLoggedIn, String username) {

        this.username = username;
        this.showUserName = username != null && !username.isBlank();

        this.showLoginLink = !isLoggedIn;
        this.showLogoutButton = isLoggedIn;
        this.showRegisterLink = !isLoggedIn;
        this.showAlbums = true;
    }

    public boolean isShowUserName() {
        return showUserName;
    }

    public String getUsername() {
        return username;
    }

    public boolean isShowLoginLink() {
        return showLoginLink;
    }

    public boolean isShowLogoutButton() {
        return showLogoutButton;
    }

    public boolean isShowRegisterLink() {
        return showRegisterLink;
    }

    public boolean isShowAlbums() {
        return showAlbums;
    }
}
