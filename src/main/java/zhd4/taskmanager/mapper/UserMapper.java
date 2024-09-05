package zhd4.taskmanager.mapper;

import zhd4.taskmanager.dto.UserCreateDTO;
import zhd4.taskmanager.dto.UserDTO;
import zhd4.taskmanager.dto.UserUpdateDTO;
import zhd4.taskmanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);
}
