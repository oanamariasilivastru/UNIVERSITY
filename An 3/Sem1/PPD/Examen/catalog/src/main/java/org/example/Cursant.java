package org.example;

public class Cursant {
    public int id;
    public double medie;

    public Cursant(int id, double medie) {
        this.id = id;
        this.medie = medie;
    }

    public int getId() {
        return id;
    }

    public double getMedie() {
        return medie;
    }

    @Override
    public String toString() {
        return "(" + id + ", med=" + medie + ")";
    }

}
