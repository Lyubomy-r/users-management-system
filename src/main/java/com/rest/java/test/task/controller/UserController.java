package com.rest.java.test.task.controller;

import com.rest.java.test.task.entity.User;
import com.rest.java.test.task.entity.dto.DataResponse;
import com.rest.java.test.task.entity.dto.GeneralResponse;
import com.rest.java.test.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

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
    public DataResponse<CollectionModel<User>> showAllUsers() {
        List<User> usersList = userService.findAll();

        CollectionModel<User> result = userService.addLinksCollectionModel(usersList);
        DataResponse<CollectionModel<User>> response = new DataResponse<>(result);

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
                .showAllUsers()).withRel("allUsers");
        result.add(linkAllUsers);

        DataResponse<EntityModel<GeneralResponse>> response = new DataResponse<>(result);

        logger.info("From UsersController method -deleteUser- (/api/v1/users/{userId}) delete User with userId: {}.", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<DataResponse<CollectionModel<User>>> searchByBirthDateRange(@RequestParam String from,
                                                                                      @RequestParam String to) {
        List<User> userList = userService.searchByBirthDateRange(from, to);
        CollectionModel<User> result = userService.addLinksCollectionModel(userList);

        DataResponse<CollectionModel<User>> response = new DataResponse<>(result);

        logger.info("From UsersController method -searchByBirthDateRange- (/api/v1/users/search) " +
                "search Users By BirthDate Range from: {} - to: {} .", from, to);
        return ResponseEntity.ok(response);
    }
}
