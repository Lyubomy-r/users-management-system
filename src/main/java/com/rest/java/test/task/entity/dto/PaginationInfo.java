package com.rest.java.test.task.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaginationInfo {
    private int page;
    private int size;
    private int totalPages;

}
