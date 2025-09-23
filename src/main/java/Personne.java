package models;

public class Personne extends BaseModels{

    private String nom;

    public Personne(int id, String nom) {
        super(id);
        this.nom = nom;
    }

    public Personne (String nom) 
    {
        this.nom = nom;
    }

    public Personne () {}

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    
}