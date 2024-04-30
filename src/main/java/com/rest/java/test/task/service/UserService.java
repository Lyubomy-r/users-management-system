package com.rest.java.test.task.service;

import com.rest.java.test.task.entity.User;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User save(User user);

    User updateUserFields(Long id, User userUpdate);

    void delete(Long id);

    List<User> searchByBirthDateRange(String from,
                                      String to);

    CollectionModel<User> addLinksCollectionModel(List<User> usersList);

    void addLinksToEntityModel(User user);
}
