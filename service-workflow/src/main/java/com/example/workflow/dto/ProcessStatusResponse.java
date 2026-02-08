package com.example.workflow.dto;// dto/ProcessStatusResponse.java


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStatusResponse {
    private String processInstanceId;
    private String businessKey;
    private boolean ended;
}