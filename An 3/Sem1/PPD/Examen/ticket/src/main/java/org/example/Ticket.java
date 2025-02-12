package org.example;

public class Ticket {
    private int id;
    private int service1;
    private int service2;
    public Ticket(int id, int service1, int service2) {
        this.id = id;
        this.service1 = service1;
        this.service2 = service2;
    }

    public int getId() {
        return id;
    }
    public int getService1() {
        return service1;
    }
    public int getService2() {
        return service2;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", s1=" + service1 +
                ", s2=" + service2 +
                '}';
    }

}
