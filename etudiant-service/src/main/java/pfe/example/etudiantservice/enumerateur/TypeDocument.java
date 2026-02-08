package pfe.example.etudiantservice.enumerateur;

public enum TypeDocument {
    CARTE_IDENTITE("Carte d'identité ou Passeport"),
    DIPLOME_BAC("Diplôme du Baccalauréat"),
    DIPLOME_LICENCE("Diplôme de Licence"),
    DIPLOME_MASTER("Diplôme de Master"),
    RELEVE_NOTES("Relevé de notes"),
    CERTIFICAT_NAISSANCE("Certificat de naissance"),
    AUTRE("Autre document");

    private final String libelle;

    TypeDocument(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
