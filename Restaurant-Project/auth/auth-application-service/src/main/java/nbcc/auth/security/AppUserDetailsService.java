package nbcc.auth.security;

import nbcc.auth.entity.UserLoginEntity;
import nbcc.auth.repository.UserLoginJPARepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserLoginJPARepository userLoginJPARepository;

    public AppUserDetailsService(UserLoginJPARepository userLoginJPARepository) {
        this.userLoginJPARepository = userLoginJPARepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        UserLoginEntity user = userLoginJPARepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
        return new AppUserDetails(user);
    }
}