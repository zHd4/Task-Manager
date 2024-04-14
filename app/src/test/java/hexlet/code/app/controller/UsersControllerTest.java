package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.component.DefaultUserProperties;
import hexlet.code.app.dto.UserCreateDTO;
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        MvcResult result = mockMvc.perform(get("/api/users/{id}", testUser.getId()))
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

        userData.setFirstName(FAKER.name().firstName());
        userData.setLastName(FAKER.name().lastName());
        userData.setEmail(FAKER.internet().emailAddress());
        userData.setPassword(FAKER.internet().password(5, 30));

        String json = objectMapper.writeValueAsString(userData);

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> userOptional = userRepository.findByEmail(userData.getEmail());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getFirstName()).isEqualTo(userData.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userData.getLastName());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail());

        assertThat(user.getPassword()).isNotEqualTo(userData.getPassword());
    }

    @Test
    public void testCreateDefaultUser() throws Exception {
        String email = defaultUserProperties.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        assertThat(userOptional.isPresent()).isTrue();
        User user = userOptional.get();

        assertThat(user.getPassword()).isNotEqualTo(defaultUserProperties.getPassword());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO userData = new UserUpdateDTO();

        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));
        userData.setPassword(JsonNullable.of(FAKER.internet().password(5, 30)));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
        assertThat(user.getPassword()).isNotEqualTo(userData.getPassword().get());
    }

    @Test
    public void testUpdateWithoutEmail() throws Exception {
        userRepository.save(testUser);

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setPassword(JsonNullable.of(FAKER.internet().password(5, 30)));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getPassword()).isNotEqualTo(userData.getPassword().get());
    }

    @Test
    public void testUpdateWithoutPassword() throws Exception {
        String password = testUser.getPassword();
        String passwordDigest = passwordEncoder.encode(password);

        testUser.setPassword(passwordDigest);
        userRepository.save(testUser);

        testUser.setPassword(password);

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isPresent()).isTrue();

        User user = userOptional.get();

        System.out.println("Actual: " + user.getEmail());
        System.out.println("Expected: " + userData.getEmail().get());
        System.out.println("\n\n");

        assertThat(user.getEmail()).isEqualTo(userData.getEmail().get());
        assertThat(user.getPassword()).isNotEqualTo(testUser.getPassword());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);

        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        Optional<User> userOptional = userRepository.findById(testUser.getId());

        assertThat(userOptional.isEmpty()).isTrue();
    }
}
