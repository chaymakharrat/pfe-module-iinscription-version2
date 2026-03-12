package com.example.workflow.dto;


import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EnregistrerPaiementRequest {
    private double montant;
    private String modePaiement; // EN_PRESENTIEL | EN_LIGNE
    private String agentEmail;
}
