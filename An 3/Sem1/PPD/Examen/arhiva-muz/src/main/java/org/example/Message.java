package org.example;

import java.util.Objects;

public class Message {
    private String titluPiesa;
    private String numeCompozitor;

    private String departament;

    public Message(String titluPiesa, String numeCompozitor, String departament) {
        this.titluPiesa = titluPiesa;
        this.numeCompozitor = numeCompozitor;
        this.departament = departament;
    }

    public String getTitluPiesa() {
        return titluPiesa;
    }

    public void setTitluPiesa(String titluPiesa) {
        this.titluPiesa = titluPiesa;
    }

    public String getNumeCompozitor() {
        return numeCompozitor;
    }

    public void setNumeCompozitor(String numeCompozitor) {
        this.numeCompozitor = numeCompozitor;
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }

    @Override
    public String toString() {
        return "Message{" +
                "titluPiesa='" + titluPiesa + '\'' +
                ", numeCompozitor='" + numeCompozitor + '\'' +
                ", departament='" + departament + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(getTitluPiesa(), message.getTitluPiesa()) && Objects.equals(getNumeCompozitor(), message.getNumeCompozitor()) && Objects.equals(getDepartament(), message.getDepartament());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitluPiesa(), getNumeCompozitor(), getDepartament());
    }
}
