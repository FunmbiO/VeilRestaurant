package nbcc.resto.config;

import nbcc.auth.repository.UserLoginJPARepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserLoginJPARepository userLoginJPARepository;

    public UserDetailsServiceImpl(UserLoginJPARepository userLoginJPARepository) {
        this.userLoginJPARepository = userLoginJPARepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var entity = userLoginJPARepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountLocked(entity.isLocked())
                .disabled(!entity.isEnabled())
                .build();
    }
}