package com.rest.java.test.task.dao;

import com.rest.java.test.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.birthDate BETWEEN :from AND :to")
    List<User> searchByBirthDateRange(@Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

}
