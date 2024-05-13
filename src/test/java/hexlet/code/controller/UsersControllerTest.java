package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.DefaultUserProperties;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(testUser);

        MockHttpServletRequestBuilder request = get("/api/users")
                .with(SecurityMockMvcRequestPostProcessors.user("admin"));

        MvcResult result = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        MockHttpServletRequestBuilder request = get("/api/users/{id}", testUser.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        MvcResult result = mockMvc.perform(request)
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
        UserCreateDTO userData = new UserCreateDTO();

        userData.setFirstName(testUser.getFirstName());
        userData.setLastName(testUser.getLastName());
        userData.setEmail(testUser.getEmail());
        userData.setPassword(testUser.getPassword());

        String json = objectMapper.writeValueAsString(userData);

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(SecurityMockMvcRequestPostProcessors.user("user"));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> userOptional = userRepository.findByEmail(userData.getEmail());

        assertThat(userOptional).isPresent();

        User user = userOptional.get();

        assertThat(user.getFirstName()).isEqualTo(userData.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userData.getLastName());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail());

        assertThat(user.getPassword()).isNotEqualTo(userData.getPassword());
    }

    @Test
    public void testCreateDefaultUser() {
        String email = defaultUserProperties.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        assertThat(userOptional).isPresent();
        User user = userOptional.get();

        assertThat(user.getPassword()).isNotEqualTo(defaultUserProperties.getPassword());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO userData = new UserUpdateDTO();

        userData.setFirstName(JsonNullable.of(FAKER.name().firstName()));
        userData.setLastName(JsonNullable.of(FAKER.name().lastName()));
        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));
        userData.setPassword(JsonNullable.of(FAKER.internet().password(5, 30)));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData))
                .with(SecurityMockMvcRequestPostProcessors.user(testUser.getUsername()));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional).isPresent();

        User user = userOptional.get();

        assertThat(user.getFirstName()).isEqualTo(userData.getFirstName().get());
        assertThat(user.getLastName()).isEqualTo(userData.getLastName().get());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
        assertThat(user.getPassword()).isNotEqualTo(userData.getPassword().get());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);

        MockHttpServletRequestBuilder request = delete("/api/users/{id}", testUser.getId())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser.getUsername()));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional).isEmpty();
    }
}
