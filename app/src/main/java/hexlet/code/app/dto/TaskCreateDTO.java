package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private long index;
    private long assigneeId;

    @NotBlank
    private String title;

    private String content;
    private String status;
}
