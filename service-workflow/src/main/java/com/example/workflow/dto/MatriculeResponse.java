package com.example.workflow.dto;// dto/MatriculeResponse.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculeResponse {
    private String matricule;
    private Long studentId;
}