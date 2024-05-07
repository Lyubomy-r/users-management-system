package com.rest.java.test.task.service;

import com.rest.java.test.task.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;

public interface UserService {

    Page<User> findAll(int page, int size);

    User findById(Long id);

    User save(User user);

    User updateUserFields(Long id, User userUpdate);

    void delete(Long id);

    Page<User> searchByBirthDateRange(int page,
                                      int size,
                                      String from,
                                      String to);

    CollectionModel<User> addLinksCollectionModel(Page<User> usersList,
                                                  int page,
                                                  int size);

    CollectionModel<User> addLinksCollectionModelSearch(Page<User> usersList,
                                                        int page,
                                                        int size,
                                                        String from,
                                                        String to);

    void addLinksToEntityModel(User user);
}
