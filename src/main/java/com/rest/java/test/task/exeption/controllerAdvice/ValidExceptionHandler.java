package com.rest.java.test.task.exeption.controllerAdvice;

import com.rest.java.test.task.exeption.model.DataErrorResponse;
import com.rest.java.test.task.exeption.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ControllerAdvice
public class ValidExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ValidExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<DataErrorResponse<ErrorResponse>> handlerException(MethodArgumentNotValidException exc) {
        ErrorResponse userErrorResponse = new ErrorResponse();
        userErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        userErrorResponse.setMessage(Objects.requireNonNull(exc.getFieldError()).getDefaultMessage());
        userErrorResponse.setTimeStamp(new Timestamp(System.currentTimeMillis()));
        DataErrorResponse<ErrorResponse> response=new DataErrorResponse<>(userErrorResponse);
        logger.warn("From ValidExceptionHandler method -handlerException- send message error ({})",
                userErrorResponse.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<DataErrorResponse<ErrorResponse>> handlerConstraintViolationException(ConstraintViolationException exc) {
        ErrorResponse userErrorResponse = new ErrorResponse();
        userErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        Set<ConstraintViolation<?>> violations = exc.getConstraintViolations();
        Optional<String> message = violations.stream().map(ConstraintViolation::getMessage).findFirst();

        userErrorResponse.setMessage(Objects.requireNonNull(message.orElse(exc.getMessage())));
        userErrorResponse.setTimeStamp(new Timestamp(System.currentTimeMillis()));

        DataErrorResponse<ErrorResponse> response=new DataErrorResponse<>(userErrorResponse);
        logger.warn("From ValidExceptionHandler method -handlerConstraintViolationException- send message error ({})",
                userErrorResponse.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
