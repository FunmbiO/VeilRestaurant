package nbcc.auth.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EntityMapper<REQUEST, RESPONSE, ENTITY> {

    default List<RESPONSE> toDTO(Collection<ENTITY> entities){
        return entities.stream().map(this::toDTO).toList();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default Optional<RESPONSE> toDTO(Optional<ENTITY> entity){
        return entity.map(this::toDTO);
    }

    RESPONSE toDTO(ENTITY entity);

    ENTITY toEntity(REQUEST request);

}
