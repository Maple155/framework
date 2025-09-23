package models;

public class Depense extends BaseModels{
    int id_prevision;
    int montant;
    
    public Depense () {}
    
    public Depense (int id_prevision, int montant) {
        this.setId_prevision(id_prevision);
        this.setMontant(montant);
    }

    public Depense (int id, int id_prevision, int montant) {
        super(id);
        this.setId_prevision(id_prevision);
        this.setMontant(montant);
    }

    public int getId_prevision() {
        return id_prevision;
    }
    public void setId_prevision(int id_prevision) {
        this.id_prevision = id_prevision;
    }

    public int getMontant() {
        return montant;
    }
    public void setMontant(int montant) {
        this.montant = montant;
    }
}   
