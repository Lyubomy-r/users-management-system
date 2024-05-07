package com.rest.java.test.task.service.imp;

import com.rest.java.test.task.dao.UserRepository;
import com.rest.java.test.task.entity.User;
import com.rest.java.test.task.exeption.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceImpTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserServiceImp usersServiceImp;

    private User user;

    @BeforeEach
    void createUserOne() {
        user = User.builder()
                .id(1L)
                .firstName("Ronald")
                .lastName("Serous")
                .email("sewewt@code.com")
                .birthDate(LocalDate.of(2000, 4, 10))
                .address("2194 Richmond Terrace Staten Island, NY  10302")
                .phoneNumber("+130020050002")
                .build();
    }

    @Test
    @DisplayName("Test UsersServiceImp method save")
    void testMethodSave() {

        when(userRepository.save(any())).thenReturn(user);

        User newUser = usersServiceImp.save(user);

        assertAll(
                () -> assertNotNull(newUser),
                () -> assertEquals(user.getId(), newUser.getId()),
                () -> assertEquals(user.getFirstName(), newUser.getFirstName()),
                () -> assertEquals(user.getLastName(), newUser.getLastName()),
                () -> assertEquals(user.getPhoneNumber(), newUser.getPhoneNumber()),
                () -> assertEquals(user.getBirthDate(), newUser.getBirthDate())
        );
    }

    @Test
    @DisplayName("Test UsersServiceImp method updateUserFields")
    void testMethodUpdateUserFields() {
        Long userId = user.getId();

        User userRequest = User.builder()
                .firstName("Robert")
                .lastName("Bert")
                .phoneNumber("+130020050222")
                .build();

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);

        User updateUser = usersServiceImp.updateUserFields(userId, userRequest);

        assertAll(
                () -> assertNotNull(updateUser),
                () -> assertEquals(userId, updateUser.getId()),
                () -> assertEquals(userRequest.getFirstName(), updateUser.getFirstName()),
                () -> assertEquals(userRequest.getLastName(), updateUser.getLastName()),
                () -> assertEquals(userRequest.getPhoneNumber(), updateUser.getPhoneNumber()),
                () -> assertEquals(user.getBirthDate(), updateUser.getBirthDate())
        );
    }

    @Test
    @DisplayName("Test UsersServiceImp exception in method updateUserFields")
    void testExceptionInMethodUpdateUserFields() {
        Long userId = user.getId();

        UserException errorUserIdNotExist = assertThrows(UserException.class,
                () -> usersServiceImp.updateUserFields(userId, user));

        assertEquals("User with user id:" + userId + " is not exist", errorUserIdNotExist.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorUserIdNotExist.getHttpStatus());

        UserException errorNullUserId = assertThrows(UserException.class,
                () -> usersServiceImp.updateUserFields(-1L, user));

        assertEquals("Write correct User id.", errorNullUserId.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorNullUserId.getHttpStatus());
    }

    @Test
    @DisplayName("Test UsersServiceImp method findAll")
    void testMethodFindAll() {
        User userSecond = User.builder()
                .id(2L)
                .firstName("Stewen")
                .lastName("Rasul")
                .email("srasult@code.com")
                .birthDate(LocalDate.of(2001, 3, 17))
                .address("2302 Arthur Kill Rd Staten Island, NY  10309")
                .phoneNumber("+130020050022")
                .build();

        List<User> createUserList = List.of(user, userSecond);
        Page<User> userPage = new PageImpl<>(createUserList);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> userList = usersServiceImp.findAll(0, 10);
        List<User> userListFromDB = userList.getContent();

        assertAll(
                () -> assertNotNull(userListFromDB),
                () -> assertFalse(userListFromDB.isEmpty()),
                () -> assertEquals(2, userListFromDB.size()),
                () -> assertTrue(userListFromDB.contains(user)),
                () -> assertTrue(userListFromDB.contains(userSecond))
        );

        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Test UsersServiceImp method findById")
    void testMethodFindById() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        User serviceUser = usersServiceImp.findById(user.getId());

        assertAll(
                () -> assertNotNull(serviceUser),
                () -> assertEquals(user.getId(), serviceUser.getId())
        );

        Long badUserId = user.getId() + 2;
        UserException error = assertThrows(UserException.class, () ->
                usersServiceImp.findById(badUserId));

        assertEquals("User with user id:" + badUserId + " is not exist", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus());

        verify(userRepository, times(2)).findById(any());
    }

    @Test
    @DisplayName("Test UsersServiceImp method delete")
    void testMethodDelete() {

        Long responseId = user.getId();

        when(userRepository.findById(responseId)).thenReturn(Optional.of(user));

        usersServiceImp.delete(responseId);

        verify(userRepository).delete(user);

        when(userRepository.findById(responseId)).thenReturn(Optional.empty());

        UserException error = assertThrows(UserException.class, () -> usersServiceImp.delete(responseId));

        assertEquals("User with users id: " + responseId + " is not exist.", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus());
    }

    @Test
    @DisplayName("Test UsersServiceImp method searchByBirthDateRange")
    void testMethodSearchByBirthDateRange() {

        User userSecond = User.builder()
                .id(2L)
                .firstName("Stewen")
                .lastName("Rasul")
                .email("srasult@code.com")
                .birthDate(LocalDate.of(2001, 3, 17))
                .address("2302 Arthur Kill Rd Staten Island, NY  10309")
                .phoneNumber("+130020050022")
                .build();

        List<User> creatUserList = List.of(user, userSecond);

        String rangFrom = "1999-03-17";
        String rangTo = "2010-06-20";
        Page<User> userPage = new PageImpl<>(creatUserList);
        when(userRepository.searchByBirthDateRange(any(Pageable.class), any(), any())).thenReturn(userPage);

        Page<User> userList = usersServiceImp.searchByBirthDateRange(0, 10, rangFrom, rangTo);
        List<User> userListFromDB = userList.getContent();
        assertAll(
                () -> assertNotNull(userListFromDB),
                () -> assertFalse(userListFromDB.isEmpty()),
                () -> assertEquals(2, userListFromDB.size()),
                () -> assertTrue(userListFromDB.contains(user)),
                () -> assertTrue(userListFromDB.contains(userSecond))
        );

        verify(userRepository, times(1)).searchByBirthDateRange(any(Pageable.class), any(), any());

    }

    @Test
    @DisplayName("Test UsersServiceImp exception in method searchByBirthDateRange")
    void testExceptionInMethodSearchByBirthDateRange() {

        LocalDate currenData = LocalDate.now();
        String rangFrom = "2010-06-20";
        String rangTo = "1999-03-17";

        UserException errorBirthDateIsEmpty = assertThrows(UserException.class,
                () -> usersServiceImp.searchByBirthDateRange(0, 10, null, null));

        assertEquals("Birth Date is empty.", errorBirthDateIsEmpty.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorBirthDateIsEmpty.getHttpStatus());

        UserException errorRangFromIsBefore = assertThrows(UserException.class,
                () -> usersServiceImp.searchByBirthDateRange(0, 10, rangFrom, rangTo));

        assertEquals("Birth Date range is wrong. Write correct rang.", errorRangFromIsBefore.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorRangFromIsBefore.getHttpStatus());

        String rangFromNow = currenData.plusMonths(2).toString();
        String rangToNow = currenData.plusYears(1).toString();

        UserException errorRangNowIsBefore = assertThrows(UserException.class,
                () -> usersServiceImp.searchByBirthDateRange(0, 10, rangFromNow, rangToNow));

        assertEquals("Birth Date range is wrong. Write correct rang.", errorRangNowIsBefore.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorRangNowIsBefore.getHttpStatus());
    }
}