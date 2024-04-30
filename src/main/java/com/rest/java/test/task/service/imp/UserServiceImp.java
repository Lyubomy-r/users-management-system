package com.rest.java.test.task.service.imp;

import com.rest.java.test.task.controller.UserController;
import com.rest.java.test.task.dao.UserRepository;
import com.rest.java.test.task.entity.User;
import com.rest.java.test.task.exeption.UserException;
import com.rest.java.test.task.service.UserService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    @Value("${min.user.age}")
    private Integer minAge;

    @Transactional
    @Override
    public User save(User user) {
        if (user == null) {
            logger.warn("From UserServiceImp method -save- send war message " +
                    "( User is not available or his is empty. ({})))", HttpStatus.NOT_FOUND);

            throw new UserException("User is not available or his is empty.", HttpStatus.NOT_FOUND);
        }

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            logger.warn("From UserServiceImp method -save- send war message " +
                    "( Email already exists. ({})))", HttpStatus.NO_CONTENT.name());
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        LocalDate dateTime = LocalDate.now();
        Period period = Period.between(user.getBirthDate(), dateTime);
        Integer age = period.getYears();
        if (age >= minAge) {
            User newUser = userRepository.save(user);
            logger.info("From UsersServiceImp method -save- Return new save User from Data Base.");
            return newUser;
        } else {
            logger.warn("From UserServiceImp method -save- send war message " +
                    "(You are not yet of the appropriate age. Minimum age: {}. ({})))", minAge, HttpStatus.NO_CONTENT.name());
            throw new UserException("You are not yet of the appropriate age. Minimum age: " + minAge + ".", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public User updateUserFields(Long userId, User userUpdate) {
        if (userId > 0) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("From UserServiceImp method -updateUserFields- send war message " +
                                "(User with users id: {} is not exist. ({})))", userId, HttpStatus.BAD_REQUEST);
                        return new UserException("User with user id:" + userId + " is not exist", HttpStatus.BAD_REQUEST);
                    });

            if (userUpdate.getFirstName() != null) {
                user.setFirstName(userUpdate.getFirstName());
            }
            if (userUpdate.getLastName() != null) {
                user.setLastName(userUpdate.getLastName());
            }
            if (userUpdate.getEmail() != null) {
                user.setEmail(userUpdate.getEmail());
            }
            if (userUpdate.getAddress() != null) {
                user.setAddress(userUpdate.getAddress());
            }
            if (userUpdate.getBirthDate() != null) {
                user.setBirthDate(userUpdate.getBirthDate());
            }
            if (userUpdate.getPhoneNumber() != null) {
                user.setPhoneNumber(userUpdate.getPhoneNumber());
            }

            userRepository.save(user);
            logger.info("From UserServiceImp method -updateUserFields- Made update User field in Data Base.");
            User updateUser = userRepository.save(user);
            return updateUser;
        } else {
            logger.warn("From UserServiceImp method -updateUserFields- send war message " +
                    "( User id is empty ({})))", HttpStatus.BAD_REQUEST);

            throw new UserException("Write correct User id.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> userList = userRepository.findAll();

        logger.info("From UserServiceImp method -findAll- return List of User .");
        return userList;
    }

    @Override
    public User findById(Long userId) {
        if (userId > 0) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("From UserServiceImp method -updateUserFields- send war message " +
                                "(User with users id: {} is not exist. ({})))", userId, HttpStatus.BAD_REQUEST);
                        return new UserException("User with user id:" + userId + " is not exist", HttpStatus.BAD_REQUEST);
                    });

            logger.info("From UserServiceImp method -findById- return User by id: {} ", userId);
            return user;
        } else {
            logger.warn("From UserServiceImp method -findById- send war message " +
                    "( Write correct User id.({})))", HttpStatus.BAD_REQUEST);
            throw new UserException("Write correct User id.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        if (userId > 0) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("From UserServiceImp method -delete- send war message " +
                                "(User with users id: {} is not exist. ({})))", userId, HttpStatus.BAD_REQUEST);
                        return new UserException("User with users id: " + userId + " is not exist.", HttpStatus.BAD_REQUEST);
                    });

            userRepository.delete(user);
            logger.info("From UserServiceImp method -delete- return message (User with userId: {} was deleted.).", userId);
        } else {
            logger.warn("From UserServiceImp method -delete- send war message " +
                    "( Write correct User id. ({})))", HttpStatus.BAD_REQUEST);
            throw new UserException("Write correct User id.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<User> searchByBirthDateRange(String from, String to) {
        if (from != null && to != null) {
            LocalDate currenData = LocalDate.now();
            LocalDate dateFrom = LocalDate.parse(from);
            LocalDate dateTo = LocalDate.parse(to);
            boolean validationWithCurrentDate = dateFrom.isBefore(currenData) && dateTo.isBefore(currenData);
            boolean validationFromTo = dateFrom.isBefore(dateTo);
            if (validationWithCurrentDate && validationFromTo) {
                List<User> userList = userRepository.searchByBirthDateRange(dateFrom, dateTo);
                logger.info("From UserServiceImp method -searchByBirthDateRange- return List of Users.");
                return userList;
            } else {
                logger.warn("From UserServiceImp method -searchByBirthDateRange- send war message " +
                        "(Birth Date range is wrong. Write correct rang. ({})))", HttpStatus.BAD_REQUEST);
                throw new UserException("Birth Date range is wrong. Write correct rang.", HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.warn("From UserServiceImp method -searchByBirthDateRange- send war message " +
                    "(Birth Date is empty. ({})))", HttpStatus.BAD_REQUEST);
            throw new UserException("Birth Date is empty.", HttpStatus.BAD_REQUEST);
        }
    }


    public Integer getMinAge() {
        return minAge;
    }

    @Override
    public CollectionModel<User> addLinksCollectionModel(List<User> usersList) {

        List<User> usersWhitLink = usersList.stream().map(user -> {
                    Link selfLink = linkTo(UserController.class).slash(user.getId()).withSelfRel();
                    user.add(selfLink);
                    return user;
                }

        ).collect(Collectors.toList());
        Link link = linkTo(UserController.class).withRel("showAllUsers");

        CollectionModel<User> result = CollectionModel.of(usersWhitLink, link);
        logger.info("From UserServiceImp method -addLinksCollectionModel- return List of Users with links.");
        return result;
    }

    @Override
    public void addLinksToEntityModel(User user) {
        Link linkSelf = linkTo(methodOn(UserController.class)
                .getUserById(user.getId())).withSelfRel();
        user.add(linkSelf);
        Link linkAllUsers = linkTo(methodOn(UserController.class)
                .showAllUsers()).withRel("allUsers");
        user.add(linkAllUsers);
        logger.info("From UserServiceImp method -addLinksToEntityModel- add Links to User.");
    }

}
