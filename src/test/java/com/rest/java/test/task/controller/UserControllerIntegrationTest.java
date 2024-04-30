package com.rest.java.test.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.java.test.task.JavaTestApplication;
import com.rest.java.test.task.dao.UserRepository;
import com.rest.java.test.task.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = JavaTestApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Value("${min.user.age}")
    private Integer minAge;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    private User userSecond;

    @BeforeEach
    void createUsers() {

        user = User.builder()
                .firstName("Ronald")
                .lastName("Serous")
                .email("sewewt@code.com")
                .birthDate(LocalDate.of(2000, 4, 10))
                .address("2194 Richmond Terrace Staten Island, NY  10302")
                .phoneNumber("+130020050002")
                .build();

        userSecond = User.builder()
                .firstName("Stewen")
                .lastName("Rasul")
                .email("srasult@code.com")
                .birthDate(LocalDate.of(2001, 3, 17))
                .address("2302 Arthur Kill Rd Staten Island, NY  10309")
                .phoneNumber("+130020050022")
                .build();
    }

    @AfterEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration test UserController method showAllUsers")
    void testMethodShowAllUsers() throws Exception {
        userRepository.save(user);
        userRepository.save(userSecond);

        mvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[*].id",
                        containsInAnyOrder(user.getId().intValue(), userSecond.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data.content[1].email", is(userSecond.getEmail())));
    }

    @Test
    @DisplayName("Integration test UserController method getUserById")
    void testMethodGetUserById() throws Exception {
        userRepository.save(user);
        Integer userId = user.getId().intValue();

        mvc.perform(get("/api/v1/users/{userId}",userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(userId)))
                .andExpect(jsonPath("$.data.email", is(user.getEmail())));

        mvc.perform(get("/api/v1/users/{userId}", 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("User with user id:" + 10 + " is not exist"));

        mvc.perform(get("/api/v1/users/{userId}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Write correct User id."));
    }

    @Test
    @DisplayName("Integration test UserController method createUser")
    void testMethodCreateUser() throws Exception {

        LocalDate notValidDate = LocalDate.now().minusYears(10);
        userSecond.setBirthDate(notValidDate);

        String newUser = objectMapper.writeValueAsString(user);
        String newUserSecond = objectMapper.writeValueAsString(userSecond);

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.birthDate", is(user.getBirthDate().toString())))
                .andExpect(jsonPath("$.data.email", is(user.getEmail())));

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Email already exists"));

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserSecond))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message")
                        .value("You are not yet of the appropriate age. Minimum age: " + minAge + "."));
    }

    @Test
    @DisplayName("Integration test UserController method updateUserFields")
    void testMethodUpdateUserFields() throws Exception {
        userRepository.save(user);
        Long userId = user.getId();
        Long wrongId = userId + 2;

        User userRequest = User.builder()
                .firstName("Robert")
                .lastName("Bert")
                .phoneNumber("+130020050222")
                .build();

        String newUser = objectMapper.writeValueAsString(userRequest);

        mvc.perform(patch("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(userId.intValue())))
                .andExpect(jsonPath("$.data.firstName", is(userRequest.getFirstName())))
                .andExpect(jsonPath("$.data.phoneNumber", is(userRequest.getPhoneNumber())))
                .andExpect(jsonPath("$.data.lastName", is(userRequest.getLastName())));

        mvc.perform(get("/api/v1/users/{userId}", wrongId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("User with user id:" + wrongId + " is not exist"));

        mvc.perform(get("/api/v1/users/{userId}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Write correct User id."));
    }

    @Test
    @DisplayName("Integration test UserController method deleteUser")
    void testMethodDeleteUser() throws Exception {
        userRepository.save(user);
        Long userId = user.getId();
        Long wrongId = userId + 2;

        mvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message", is("Users with id: " + userId + " was deleted successfully.")));

        mvc.perform(get("/api/v1/users/{userId}", wrongId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("User with user id:" + wrongId + " is not exist"));

        mvc.perform(get("/api/v1/users/{userId}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Write correct User id."));
    }

    @Test
    @DisplayName("Integration test UserController method searchByBirthDateRange")
    void testMethodSearchByBirthDateRange() throws Exception {
        userRepository.save(user);
        userRepository.save(userSecond);

        String rangFrom = "1999-03-17";
        String rangTo = "2010-06-20";

        mvc.perform(get("/api/v1/users/search")
                        .param("from", rangFrom)
                        .param("to", rangTo))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[*].id",
                        containsInAnyOrder(user.getId().intValue(), userSecond.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data.content[1].email", is(userSecond.getEmail())));
    }

    @Test
    @DisplayName("Integration test UserController exception in method searchByBirthDateRange")
    void testExceptionInMethodSearchByBirthDateRange() throws Exception {
        LocalDate currenData = LocalDate.now();
        String wrongRangFrom = "2010-06-20";
        String wrongRangTo = "1999-03-17";

        String wrongRangFromNow = currenData.plusMonths(2).toString();
        String wrongRangToNow = currenData.plusYears(1).toString();

        mvc.perform(get("/api/v1/users/search")
                        .param("from", wrongRangFrom)
                        .param("to", wrongRangTo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Birth Date range is wrong. Write correct rang."));

        mvc.perform(get("/api/v1/users/search")
                        .param("from", wrongRangFromNow)
                        .param("to", wrongRangToNow))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Birth Date range is wrong. Write correct rang."));
    }

    @Test
    @DisplayName("Integration test UserController validation exception in method createUser")
    void testValidationExceptionInMethodCreateUser() throws Exception{

        LocalDate notValidDate = LocalDate.now().minusYears(10);
        userSecond.setBirthDate(notValidDate);

        User testValidationUserFields = User.builder()
                .firstName("Robert")
                .lastName("Serous")
                .email("sewewt@code.com")
                .birthDate(LocalDate.of(2024, 7, 10))
                .address("2194 Richmond Terrace Staten Island, NY  10302")
                .phoneNumber("+130020050002")
                .build();

        String newUser = objectMapper.writeValueAsString(testValidationUserFields);

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}