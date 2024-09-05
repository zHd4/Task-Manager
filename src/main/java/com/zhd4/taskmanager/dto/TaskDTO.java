package com.zhd4.taskmanager.dto;

//import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
//@JsonInclude(JsonInclude.Include.CUSTOM)
public class TaskDTO {
    private long id;
    private long index;
    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;

    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
