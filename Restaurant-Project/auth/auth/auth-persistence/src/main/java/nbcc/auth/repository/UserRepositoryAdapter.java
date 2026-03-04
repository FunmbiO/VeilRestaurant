package nbcc.auth.repository;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserLoginJPARepository userLoginJPARepository;

    public UserRepositoryAdapter(PasswordEncoder passwordEncoder, UserMapper userMapper, UserLoginJPARepository userLoginJPARepository) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userLoginJPARepository = userLoginJPARepository;
    }

    @Override
    public UserResponse create(UserRegistration userLogin) {
        userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));
        var entity = userMapper.toEntity(userLogin);
        entity = userLoginJPARepository.save(entity);
        return userMapper.toDTO(entity);
    }

    @Override
    public Optional<UserResponse> get(long id) {
        var entity = userLoginJPARepository.findById(id);
        return userMapper.toDTO(entity);
    }

    @Override
    public Optional<UserResponse> get(String username) {
        var entity = userLoginJPARepository.findByUsernameIgnoreCase(username);
        return userMapper.toDTO(entity);
    }

    @Override
    public boolean isValid(String username, String password) {
        var optionalUser = userLoginJPARepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();

            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @Override
    public boolean userExists(String username) {
        return userLoginJPARepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userLoginJPARepository.existsByEmailIgnoreCase(email);
    }
}