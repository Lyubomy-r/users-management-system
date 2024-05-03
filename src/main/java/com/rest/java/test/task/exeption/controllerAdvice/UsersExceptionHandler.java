package com.rest.java.test.task.exeption.controllerAdvice;

import com.rest.java.test.task.exeption.UserException;
import com.rest.java.test.task.exeption.model.DataErrorResponse;
import com.rest.java.test.task.exeption.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;

@ControllerAdvice
public class UsersExceptionHandler  {

    Logger logger = LoggerFactory.getLogger(UsersExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<DataErrorResponse<ErrorResponse>> handlerException(UserException exc) {
        ErrorResponse errorResponse = new ErrorResponse(
                exc.getHttpStatus().value(),
                exc.getMessage(),
                new Timestamp(System.currentTimeMillis()));
        DataErrorResponse<ErrorResponse> response=new DataErrorResponse<>(errorResponse);
        logger.warn("From UsersExceptionHandler method -handlerException- send message error ({})",
                errorResponse.getMessage());

        return new ResponseEntity<>(response, exc.getHttpStatus());
    }

}
