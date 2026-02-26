package pfe.example.finance_service.enumerateur;

public enum StatusEcheance {
    EN_ATTENTE,  // pas encore payée
    PAYE,        // paiement reçu
    IMPAYE       // date dépassée sans paiement
}
