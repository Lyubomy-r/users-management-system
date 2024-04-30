package com.rest.java.test.task.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataResponse<T> {

    private T data;

    public DataResponse(T data) {
        this.data = data;
    }

}
