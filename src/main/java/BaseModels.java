package models;

public class BaseModels {
    private int id;

    public BaseModels () {}

    public BaseModels(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
