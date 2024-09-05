package zhd4.taskmanager.mapper;

import zhd4.taskmanager.dto.UserCreateDTO;
import zhd4.taskmanager.dto.UserDTO;
import zhd4.taskmanager.dto.UserUpdateDTO;
import zhd4.taskmanager.model.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T18:24:09+0200",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 19.0.2 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl extends UserMapper {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Override
    public User map(UserCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( dto.getFirstName() );
        user.setLastName( dto.getLastName() );
        user.setEmail( dto.getEmail() );
        user.setPassword( dto.getPassword() );

        return user;
    }

    @Override
    public UserDTO map(User model) {
        if ( model == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( model.getId() );
        userDTO.setEmail( model.getEmail() );
        userDTO.setFirstName( model.getFirstName() );
        userDTO.setLastName( model.getLastName() );
        userDTO.setCreatedAt( model.getCreatedAt() );

        return userDTO;
    }

    @Override
    public void update(UserUpdateDTO dto, User model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getFirstName() != null ) {
            model.setFirstName( jsonNullableMapper.unwrap( dto.getFirstName() ) );
        }
        if ( dto.getLastName() != null ) {
            model.setLastName( jsonNullableMapper.unwrap( dto.getLastName() ) );
        }
        if ( dto.getEmail() != null ) {
            model.setEmail( jsonNullableMapper.unwrap( dto.getEmail() ) );
        }
        if ( dto.getPassword() != null ) {
            model.setPassword( jsonNullableMapper.unwrap( dto.getPassword() ) );
        }
    }
}
