package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.util.ModelGenerator;
import net.datafaker.Faker;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void beforeEach() {
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        MockHttpServletRequestBuilder request = get("/api/task_statuses")
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }


    @Test
    public void testShow() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        MockHttpServletRequestBuilder request = get("/api/task_statuses/{id}", testTaskStatus.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTaskStatus.getId()),
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();

        dto.setName(testTaskStatus.getName());
        dto.setSlug(testTaskStatus.getSlug());

        MockHttpServletRequestBuilder request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findBySlug(dto.getSlug());
        assertThat(taskStatusOptional).isPresent();

        TaskStatus status = taskStatusOptional.get();
        assertThat(status.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of(FAKER.text().text(4, 7)));

        MockHttpServletRequestBuilder request = put("/api/task_statuses/{id}", testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findById(testTaskStatus.getId());
        assertThat(taskStatusOptional).isPresent();

        TaskStatus status = taskStatusOptional.get();

        assertThat(status.getName()).isEqualTo(dto.getName());
        assertThat(status.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    public void testDelete() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        MockHttpServletRequestBuilder request = delete("/api/task_statuses")
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request).andExpect(status().isNoContent());

        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findById(testTaskStatus.getId());
        assertThat(taskStatusOptional).isEmpty();
    }
}
