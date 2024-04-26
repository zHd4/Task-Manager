package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.util.ModelGenerator;
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
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private Task testTask;

    @BeforeEach
    public void beforeEach() {
        TaskStatus status = taskStatusRepository.findBySlug("draft").get();
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();

        testTask.setTaskStatus(status);
        testTask.setLabels(Set.of());
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
        dto.setStatus("draft");
        dto.setAssigneeId(1);

        MockHttpServletRequestBuilder request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<Task> taskOptional = taskRepository.findByName(dto.getTitle());
        assertThat(taskOptional).isPresent();

        Task task = taskOptional.get();
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(dto.getStatus());
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
