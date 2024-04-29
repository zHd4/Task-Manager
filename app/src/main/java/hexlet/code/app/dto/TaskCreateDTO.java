package hexlet.code.app.dto;

import hexlet.code.app.model.Label;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    private long index;
    private JsonNullable<Long> assigneeId;

    @NotBlank
    private String title;

    private JsonNullable<String> content;
    private JsonNullable<String> status;

    private JsonNullable<Set<Label>> labels;
}
