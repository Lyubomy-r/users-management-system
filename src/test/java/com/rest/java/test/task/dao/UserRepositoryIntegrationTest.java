package com.rest.java.test.task.dao;

import com.rest.java.test.task.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void createUserOne() {
        user = User.builder()
                .firstName("Ronald")
                .lastName("Serous")
                .email("sewewt@code.com")
                .birthDate(LocalDate.of(2000, 4, 10))
                .address("2194 Richmond Terrace Staten Island, NY  10302")
                .phoneNumber("+130020050002")
                .build();
    }

    @Test
    @DisplayName("JUnit test TestEntityManager Not Null")
    public void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    @DisplayName("Test UserRepository method findByEmail")
    void testMethodFindByEmail() {
        entityManager.persist(user);

        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        assertAll(
                () -> assertTrue(userOptional.isPresent()),
                () -> assertEquals(userOptional.get().getEmail(), user.getEmail()),
                () -> assertEquals(userOptional.get().getFirstName(), user.getFirstName()),
                () -> assertEquals(userOptional.get().getId(), user.getId())
        );
    }

    @Test
    @DisplayName("Test UserRepository method findByUserId")
    void testMethodFindById() {
        entityManager.persist(user);

        Optional<User> userOptional = userRepository.findById(user.getId());

        assertAll(
                () -> assertTrue(userOptional.isPresent()),
                () -> assertEquals(userOptional.get().getEmail(), user.getEmail()),
                () -> assertEquals(userOptional.get().getFirstName(), user.getFirstName()),
                () -> assertEquals(userOptional.get().getId(), user.getId())
        );
    }

    @Test
    @DisplayName("Test UserRepository method findAll")
    void testMethodFindAll() {

        User userSecond = User.builder()
                .firstName("Stewen")
                .lastName("Rasul")
                .email("srasult@code.com")
                .birthDate(LocalDate.of(2001, 3, 17))
                .address("2302 Arthur Kill Rd Staten Island, NY  10309")
                .phoneNumber("+130020050022")
                .build();

        entityManager.persist(user);
        entityManager.persist(userSecond);

        List<User> customerList = userRepository.findAll();

        assertAll(
                () -> assertNotNull(customerList),
                () -> assertThat(customerList.size()).isEqualTo(2),
                () -> assertTrue(customerList.contains(userSecond))
        );
    }

    @Test
    @DisplayName("Test UserRepository method searchByBirthDateRange")
    void testMethodSearchByBirthDateRange() {
        User userSecond = User.builder()
                .firstName("Stewen")
                .lastName("Rasul")
                .email("srasult@code.com")
                .birthDate(LocalDate.of(2001, 3, 17))
                .address("2302 Arthur Kill Rd Staten Island, NY  10309")
                .phoneNumber("+130020050022")
                .build();

        LocalDate rangFrom = LocalDate.of(1999, 3, 17);
        LocalDate rangTo = LocalDate.of(2010, 6, 20);

        entityManager.persist(user);
        entityManager.persist(userSecond);

        List<User> customerList = userRepository.searchByBirthDateRange(rangFrom, rangTo);

        assertAll(
                () -> assertNotNull(customerList),
                () -> assertThat(customerList.size()).isEqualTo(2),
                () -> assertTrue(customerList.contains(userSecond))
        );
    }

    @Test
    @DisplayName("Test UserRepository method save")
    void testMethodSave() {
        User userFromDB = userRepository.save(user);

        assertAll(
                () -> assertNotNull(userFromDB),
                () -> assertEquals(userFromDB.getFirstName(), user.getFirstName()),
                () -> assertEquals(userFromDB.getId(), user.getId())
        );
    }

    @Test
    @DisplayName("Test UserRepository method delete")
    void testMethodDelete() {
        entityManager.persist(user);

        User userFromDB = entityManager.find(User.class, user.getId());

        userRepository.delete(userFromDB);

        User customerDelete = entityManager.find(User.class, user.getId());

        assertAll(
                () -> assertNull(customerDelete)
        );
    }
}