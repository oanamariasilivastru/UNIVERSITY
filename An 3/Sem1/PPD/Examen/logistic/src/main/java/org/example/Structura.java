package org.example;

public class Structura {
    public int idOperator;
    public int idComanda;

    public Structura(int idOperator, int idComanda) {
        this.idOperator = idOperator;
        this.idComanda = idComanda;
    }

    public int getIdOperator() {
        return idOperator;
    }

    public void setIdOperator(int idOperator) {
        this.idOperator = idOperator;
    }

    public int getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(int idComanda) {
        this.idComanda = idComanda;
    }

    @Override
    public String toString() {
        return "Structura{idOperator=" + idOperator + ", idComanda=" + idComanda + '}';
    }
}
