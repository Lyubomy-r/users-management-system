package com.rest.java.test.task.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralResponse {
    private int status;
    private String message;
    private Timestamp timeStamp;

}
