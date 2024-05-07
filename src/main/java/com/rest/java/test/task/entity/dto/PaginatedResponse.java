package com.rest.java.test.task.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginatedResponse<T> {

    private T data;
    private PaginationInfo pagination;


    public PaginatedResponse(T data, PaginationInfo pagination) {
        this.data = data;
        this.pagination = pagination;
    }
}
