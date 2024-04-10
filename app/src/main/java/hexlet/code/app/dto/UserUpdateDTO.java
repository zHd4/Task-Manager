package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    @Email
    private JsonNullable<String> email;

    @NotNull
    private JsonNullable<String> password;
}
