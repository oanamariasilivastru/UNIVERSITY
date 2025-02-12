package org.example;

public class Call {
    public int idAgent;
    public int idCall;
    public int difficulty;

    public Call(int idAgent, int idCall, int difficulty){
        this.idAgent = idAgent;
        this.idCall = idCall;
        this.difficulty = difficulty;

    }

    public int getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(int idAgent) {
        this.idAgent = idAgent;
    }

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Call{" +
                "idAgent=" + idAgent +
                ", idCall=" + idCall +
                ", difficulty=" + difficulty +
                '}';
    }
}
