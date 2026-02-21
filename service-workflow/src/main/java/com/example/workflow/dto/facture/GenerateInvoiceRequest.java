package com.example.workflow.dto;// dto/GenerateInvoiceRequest.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateInvoiceRequest {
    private Long enrollmentId;
    private Long studentId;
    private String nomDiplome;
}