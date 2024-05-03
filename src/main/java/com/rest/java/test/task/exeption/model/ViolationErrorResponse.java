package com.rest.java.test.task.exeption.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationErrorResponse<T>  {
    private int status;
    private T messages;
    private Timestamp timeStamp;
}