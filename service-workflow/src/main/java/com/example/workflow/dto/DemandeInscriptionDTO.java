package com.example.workflow.dto;// dto/DemandeInscriptionDTO.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeInscriptionDTO {
    private Long id;
    private String nomDiplome;
    private Long etudiantId;
    private Long departementId;
    private String processInstanceId;
    private LocalDateTime dateCreation;
}