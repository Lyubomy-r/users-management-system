package com.rest.java.test.task.exeption.controllerAdvice;

import com.rest.java.test.task.exeption.model.DataErrorResponse;
import com.rest.java.test.task.exeption.model.ViolationErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ValidExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<DataErrorResponse<ViolationErrorResponse<?>>> handlerConstraintViolationException(ConstraintViolationException exc) {
        ViolationErrorResponse<Set<String>> userErrorResponse = new ViolationErrorResponse<>();
        Set<ConstraintViolation<?>> violations = exc.getConstraintViolations();
        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        userErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        userErrorResponse.setMessages(messages);
        userErrorResponse.setTimeStamp(new Timestamp(System.currentTimeMillis()));

        DataErrorResponse<ViolationErrorResponse<?>> response = new DataErrorResponse<>(userErrorResponse);
        logger.warn("From ValidExceptionHandler method -handlerConstraintViolationException- send message error ({})",
                userErrorResponse.getMessages());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<DataErrorResponse<ViolationErrorResponse<?>>> handlerNotValidException(MethodArgumentNotValidException exc) {

        ViolationErrorResponse<Set<String>> userErrorResponse = new ViolationErrorResponse<>();
        Set<String> messages = exc.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toSet());

        userErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        userErrorResponse.setMessages(messages);
        userErrorResponse.setTimeStamp(new Timestamp(System.currentTimeMillis()));

        DataErrorResponse<ViolationErrorResponse<?>> response = new DataErrorResponse<>(userErrorResponse);
        logger.warn("From ValidExceptionHandler method -handlerConstraintViolationException- send message error ({})",
                userErrorResponse.getMessages());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
