package app.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.taskmanager.mapper.TaskMapper;
import app.taskmanager.repository.LabelRepository;
import app.taskmanager.repository.TaskRepository;
import app.taskmanager.repository.TaskStatusRepository;
import app.taskmanager.repository.UserRepository;
import app.taskmanager.dto.TaskCreateDTO;
import app.taskmanager.dto.TaskUpdateDTO;
import app.taskmanager.model.Label;
import app.taskmanager.model.Task;
import app.taskmanager.model.TaskStatus;
import app.taskmanager.model.User;
import app.taskmanager.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    private Task testTask;

    @BeforeEach
    public void beforeEach() {
        User assignee = userRepository.findById(1L).get();
        TaskStatus status = taskStatusRepository.findBySlug("draft").get();

        Set<Label> labels = Set.of(
                labelRepository.findByName("bug").get(),
                labelRepository.findByName("feature").get()
        );

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();

        testTask.setAssignee(assignee);
        testTask.setTaskStatus(status);
        testTask.setLabels(labels);

        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/tasks")
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testFilter() throws Exception {
        String titleCont = testTask.getName().substring(0, testTask.getName().length() / 2);
        long assigneeId = testTask.getAssignee().getId();

        String status = testTask.getTaskStatus().getSlug();
        long labelId = testTask.getLabels().stream().findFirst().get().getId();

        MockHttpServletRequestBuilder request = get("/api/tasks")
                .param("titleCont", titleCont)
                .param("assigneeId", Long.toString(assigneeId))
                .param("status", status)
                .param("labelId", Long.toString(labelId))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().contains(objectMapper.writeValueAsString(taskMapper.map(testTask)));
    }

    @Test
    public void testShow() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/tasks/{id}", testTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("title").isEqualTo(testTask.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO();

        dto.setTitle("Test title");
        dto.setStatus(JsonNullable.of("draft"));
        dto.setAssigneeId(JsonNullable.of(1L));

        MockHttpServletRequestBuilder request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<Task> taskOptional = taskRepository.findByName(dto.getTitle());
        assertThat(taskOptional).isPresent();

        Task task = taskOptional.get();
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(dto.getStatus().get());
    }

    @Test
    public void testUpdate() throws Exception {
        TaskUpdateDTO dto = new TaskUpdateDTO();

        dto.setTitle(JsonNullable.of("New title"));
        dto.setContent(JsonNullable.of("New content"));

        MockHttpServletRequestBuilder request = put("/api/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<Task> taskOptional = taskRepository.findById(testTask.getId());
        assertThat(taskOptional).isPresent();

        Task task = taskOptional.get();

        assertThat(task.getName()).isEqualTo(dto.getTitle().get());
        assertThat(task.getDescription()).isEqualTo(dto.getContent().get());
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletRequestBuilder request = delete("/api/tasks/{id}", testTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request).andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}
