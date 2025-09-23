package models;

public class Prevision extends BaseModels{
    String libelle;

    public Prevision () {}

    public Prevision (int id, String libelle) {
        super(id);
        this.setLibelle(libelle);
    }

    public Prevision (String libelle) {
        this.setLibelle(libelle);
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }


}
