package pfe.example.enrollement_module.enumerateur;

public enum TypeDocument {
    CARTE_IDENTITE("Carte d'identité ou Passeport"),
    DIPLOME_BAC("Diplôme du Baccalauréat"),
    DIPLOME_LICENCE("Diplôme de Licence"),
    DIPLOME_MASTER("Diplôme de Master"),
    RELEVE_NOTES("Relevé de notes"),
    PHOTO_IDENTITE("Photo d'identité"),
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
