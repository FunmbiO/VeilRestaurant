package nbcc.auth.mapper;

import nbcc.auth.domain.UserRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.entity.UserLoginEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {


    @Override
    public UserResponse toDTO(UserLoginEntity entity) {
        return new UserResponse()
                .setId(entity.getId())
                .setUsername(entity.getUsername())
                .setEmail(entity.getEmail())
                .setEnabled(entity.isEnabled())
                .setLocked(entity.isLocked())
                ;
    }

    @Override
    public UserLoginEntity toEntity(UserRequest request){
        return new UserLoginEntity()
                .setId(request.getId())
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setEmail(request.getEmail())
                .setEnabled(request.isEnabled())
                .setLocked(request.isLocked())
                ;
    }

    @Override
    public UserLoginEntity toEntity(UserRegistration request){
        return new UserLoginEntity()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setEmail(request.getEmail())
                .setEnabled(request.isEnabled())
                ;
    }
}
