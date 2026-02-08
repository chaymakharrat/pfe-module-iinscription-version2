// dto/HistoriqueRequest.java
package com.example.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueRequest {
    private String ancienStatus;
    private String nouveauStatus;
    private String commentaire;
    private String modifiePar;
}