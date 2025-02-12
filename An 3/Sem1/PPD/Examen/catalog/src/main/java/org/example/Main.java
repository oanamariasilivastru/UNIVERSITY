package org.example;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        final int n = 5;
        final int totalCursants = 100;

        Catalog catalog = new Catalog();
        Object lock = new Object();
        Manager manager = new Manager(catalog, lock, n);
        Thread managerThread = new Thread(manager, "Manager");
        managerThread.start();

        List<Cursant> allCursants = new LinkedList<>();
        Random rand = new Random();
        for (int i = 1; i <= totalCursants; i++) {
            int id = i;
            double media = 1 + (rand.nextDouble() * 9);
            allCursants.add(new Cursant(id, media));
        }

        List<List<Cursant>> secretariesCursants = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            secretariesCursants.add(new LinkedList<>());
        }

        for (int i = 0; i < allCursants.size(); i++) {
            secretariesCursants.get(i % n).add(allCursants.get(i));
        }

        List<Thread> secretaryThreads = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            Secretary secretary = new Secretary(secretariesCursants.get(i), catalog, manager);
            Thread t = new Thread(secretary, "Secretary-" + (i + 1));
            secretaryThreads.add(t);
            t.start();
        }

        for (Thread t : secretaryThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            managerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Execuția programului s-a încheiat.");
    }
}


class Catalog {
    private final List<Cursant> cursants = new LinkedList<>();

    public synchronized void addCursant(Cursant c) {
        cursants.add(c);
    }

    public synchronized List<Cursant> getCursantsWithMediaBelow(double threshold) {
        List<Cursant> result = new LinkedList<>();
        for (Cursant c : cursants) {
            if (c.getMedie() < threshold) {
                result.add(c);
            }
        }
        return result;
    }

    public synchronized List<Cursant> getAllCursants() {
        return new LinkedList<>(cursants);
    }
}

class Manager implements Runnable {
    private final Catalog catalog;
    private final Object lock;
    private int activeSecretaries;

    public Manager(Catalog catalog, Object lock, int numberOfSecretaries) {
        this.catalog = catalog;
        this.lock = lock;
        this.activeSecretaries = numberOfSecretaries;
    }

    public void notifyLowGrade() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void secretaryFinished() {
        synchronized (lock) {
            activeSecretaries--;
            if (activeSecretaries == 0) {
                lock.notify();
            }
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (activeSecretaries > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<Cursant> lowGrades = catalog.getCursantsWithMediaBelow(5.0);
                if (!lowGrades.isEmpty()) {
                    System.out.println("Cursanți cu media sub 5:");
                    for (Cursant c : lowGrades) {
                        System.out.println(c);
                    }
                    System.out.println("---------------------------------");
                }
            }

            System.out.println("Lista completa a cursantilor:");
            List<Cursant> allCursants = catalog.getAllCursants();
            for (Cursant c : allCursants) {
                System.out.println(c);
            }
        }
    }
}

class Secretary implements Runnable {
    private final List<Cursant> cursantsToAdd;
    private final Catalog catalog;
    private final Manager manager;

    public Secretary(List<Cursant> cursantsToAdd, Catalog catalog, Manager manager) {
        this.cursantsToAdd = cursantsToAdd;
        this.catalog = catalog;
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Cursant c : cursantsToAdd) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            catalog.addCursant(c);
            System.out.println(Thread.currentThread().getName() + " a adaugat: " + c);
            if (c.getMedie() < 5.0) {
                manager.notifyLowGrade();
            }
        }
        manager.secretaryFinished();
    }
}
