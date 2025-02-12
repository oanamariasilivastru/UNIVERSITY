package org.example;

public class Comanda {
    public int idComanda;
    public int prioritate;

    public Comanda(int idComanda, int prioritate) {
        this.idComanda = idComanda;
        this.prioritate = prioritate;
    }

    public int getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(int idComanda) {
        this.idComanda = idComanda;
    }

    public int getPrioritate() {
        return prioritate;
    }

    public void setPrioritate(int prioritate) {
        this.prioritate = prioritate;
    }
}
