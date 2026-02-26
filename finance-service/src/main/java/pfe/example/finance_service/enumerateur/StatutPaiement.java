package pfe.example.finance_service.enumerateur;

public enum StatusPaiement {
    EN_ATTENTE,   // facture générée, pas encore payée
    PARTIEL,      // paiement partiel reçu
    PAYE,         // paiement total reçu
    IMPAYE,       // délai dépassé sans paiement
    ANNULE        // facture annulée (rejet, abandon)
}
