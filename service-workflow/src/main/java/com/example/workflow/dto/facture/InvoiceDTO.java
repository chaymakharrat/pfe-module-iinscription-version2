package com.example.workflow.dto;// dto/InvoiceDTO.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Long id;
    private String numero;
    private Double montantTotal;
    private Long enrollmentId;
    private String status;
}