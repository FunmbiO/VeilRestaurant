package nbcc.auth.mapper;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserRequest;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.entity.UserLoginEntity;

public interface UserMapper extends EntityMapper<UserRequest, UserResponse, UserLoginEntity> {
    UserLoginEntity toEntity(UserRegistration request);
}
