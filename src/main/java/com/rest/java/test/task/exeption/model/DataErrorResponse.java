package com.rest.java.test.task.exeption.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataErrorResponse<T> {

    private T error;

    public DataErrorResponse(T error) {
        this.error = error;
    }
}
