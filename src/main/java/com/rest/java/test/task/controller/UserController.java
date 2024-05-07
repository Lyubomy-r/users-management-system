package com.rest.java.test.task.controller;

import com.rest.java.test.task.entity.User;
import com.rest.java.test.task.entity.dto.DataResponse;
import com.rest.java.test.task.entity.dto.GeneralResponse;
import com.rest.java.test.task.entity.dto.PaginatedResponse;
import com.rest.java.test.task.entity.dto.PaginationInfo;
import com.rest.java.test.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping
    public PaginatedResponse<CollectionModel<User>> showAllUsers(@RequestParam(defaultValue = "${default.page.number}") int page,
                                                                 @RequestParam(defaultValue = "${default.page.size}") int size) {
        Page<User> usersList = userService.findAll(page, size);
        int totalPages = usersList.getTotalPages();
        PaginationInfo paginationInfo = new PaginationInfo(page, size, totalPages);

        CollectionModel<User> result = userService.addLinksCollectionModel(usersList, page, size);

        PaginatedResponse<CollectionModel<User>> response = new PaginatedResponse<>(result, paginationInfo);

        logger.info("From UsersController method -showAllUsers- (/api/v1/users) return List of User .");
        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DataResponse<User>> getUserById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        userService.addLinksToEntityModel(user);

        DataResponse<User> response = new DataResponse<>(user);

        logger.info("From UsersController method -getUserById- (/api/v1/users/{userId}) return User with userId: {}.", userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DataResponse<User>> createUser(@Valid @RequestBody User user) {
        User newUser = userService.save(user);
        userService.addLinksToEntityModel(newUser);

        DataResponse<User> response = new DataResponse<>(newUser);

        logger.info("From UsersController method -createUser- (/api/v1/users) return new User.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<DataResponse<User>> updateUserFields(@PathVariable("userId") Long id, @RequestBody User userUpdate) {
        User result = userService.updateUserFields(id, userUpdate);
        userService.addLinksToEntityModel(result);

        DataResponse<User> response = new DataResponse<>(result);

        logger.info("From UsersController method -updateUsersFields- (/api/v1/users) updated User fields.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DataResponse<EntityModel<GeneralResponse>>> deleteUser(@PathVariable("userId") Long userId) {
        userService.delete(userId);

        GeneralResponse generalResponse = GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Users with id: " + userId + " was deleted successfully.")
                .timeStamp(new Timestamp(System.currentTimeMillis()))
                .build();
        EntityModel<GeneralResponse> result = EntityModel.of(generalResponse);

        Link linkAllUsers = linkTo(methodOn(UserController.class)
                .showAllUsers(0, 10)).withRel("allUsers");
        result.add(linkAllUsers);

        DataResponse<EntityModel<GeneralResponse>> response = new DataResponse<>(result);

        logger.info("From UsersController method -deleteUser- (/api/v1/users/{userId}) delete User with userId: {}.", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<CollectionModel<User>>> searchByBirthDateRange(@RequestParam(defaultValue = "${default.page.number}") int page,
                                                                                           @RequestParam(defaultValue = "${default.page.size}") int size,
                                                                                           @RequestParam String from,
                                                                                           @RequestParam String to) {
        Page<User> userList = userService.searchByBirthDateRange(page, size, from, to);
        int totalPages = userList.getTotalPages();

        CollectionModel<User> result = userService.addLinksCollectionModelSearch(userList, page, size, from, to);
        PaginationInfo paginationInfo = new PaginationInfo(page, size, totalPages);

        PaginatedResponse<CollectionModel<User>> response = new PaginatedResponse<>(result, paginationInfo);

        logger.info("From UsersController method -searchByBirthDateRange- (/api/v1/users/search) " +
                "search Users By BirthDate Range from: {} - to: {} .", from, to);
        return ResponseEntity.ok(response);
    }
}
