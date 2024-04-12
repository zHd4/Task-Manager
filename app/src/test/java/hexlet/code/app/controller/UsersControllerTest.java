package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(testUser);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        MvcResult result = mockMvc.perform(get("/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testCreate() throws Exception {
        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> userOptional = userRepository.findByEmail(testUser.getEmail());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getPassword()).isEqualTo(testUser.getPassword());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO userData = new UserUpdateDTO();

        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));
        userData.setPassword(JsonNullable.of(FAKER.internet().password(5, 30)));

        MockHttpServletRequestBuilder request = put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(userData.getPassword().get()));
    }

    @Test
    public void testUpdateWithoutEmail() throws Exception {
        userRepository.save(testUser);

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setPassword(JsonNullable.of(FAKER.internet().password(5, 30)));

        MockHttpServletRequestBuilder request = put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(userData.getPassword().get()));
    }

    @Test
    public void testUpdateWithoutPassword() throws Exception {
        userRepository.save(testUser);

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));

        MockHttpServletRequestBuilder request = put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(testUser.getPassword()));
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);

        mockMvc.perform(delete("/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isEmpty()).isTrue();
    }
}
