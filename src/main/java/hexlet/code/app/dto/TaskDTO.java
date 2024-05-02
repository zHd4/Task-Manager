package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private long id;
    private long index;
    private LocalDate createdAt;
    private long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
