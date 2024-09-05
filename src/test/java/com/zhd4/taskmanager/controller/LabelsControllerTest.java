package com.zhd4.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhd4.taskmanager.dto.LabelCreateDTO;
import com.zhd4.taskmanager.dto.LabelUpdateDTO;
import com.zhd4.taskmanager.mapper.LabelMapper;
import com.zhd4.taskmanager.model.Label;
import com.zhd4.taskmanager.repository.LabelRepository;
import com.zhd4.taskmanager.util.ModelGenerator;
import org.assertj.core.api.Assertions;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    private Label testLabel;

    @BeforeEach
    public void beforeEach() {
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/labels")
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        MockHttpServletRequestBuilder request = get("/api/labels/{id}", testLabel.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        LabelCreateDTO dto = new LabelCreateDTO();

        dto.setName("test-label");

        MockHttpServletRequestBuilder request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Assertions.assertThat(labelRepository.findByName(dto.getName())).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        LabelUpdateDTO dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("new-label-name"));

        MockHttpServletRequestBuilder request = put("/api/labels/{id}", testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Assertions.assertThat(labelRepository.findByName(dto.getName().get())).isPresent();
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletRequestBuilder request = delete("/api/labels/{id}", testLabel.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request).andExpect(status().isNoContent());

        Assertions.assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }

    @Test
    public void testDefaultLabels() throws Exception {
        Assertions.assertThat(labelRepository.findByName("feature")).isPresent();
        Assertions.assertThat(labelRepository.findByName("bug")).isPresent();
    }
}
