package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

enum Status {
    IN_ASTEPTARE,
    IN_SALA,
    FINALIZAT
}

public class Main {
    static class Room {
        private final int id;
        private final int totalLocuri;
        private int locuriOcupate;
        private final long timpMaxim;

        public Room(int id, int totalLocuri, long timpMaxim) {
            this.id = id;
            this.totalLocuri = totalLocuri;
            this.timpMaxim = timpMaxim;
            this.locuriOcupate = 0;
        }

        public int getId() {
            return id;
        }

        public int getTotalLocuri() {
            return totalLocuri;
        }

        public synchronized int getLocuriOcupate() {
            return locuriOcupate;
        }

        public synchronized boolean incrementOccupancy() {
            if (locuriOcupate < totalLocuri) {
                locuriOcupate++;
                return true;
            }
            return false;
        }

        public synchronized void decrementOccupancy() {
            if (locuriOcupate > 0) {
                locuriOcupate--;
            }
        }

        public long getTimpMaxim() {
            return timpMaxim;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "id=" + id +
                    ", totalLocuri=" + totalLocuri +
                    ", locuriOcupate=" + locuriOcupate +
                    ", timpMaxim=" + timpMaxim +
                    '}';
        }
    }

    static class Request {
        private final int idUser;
        private final int idSala;
        private Status status;
        private long timpIntrare;

        public Request(int idUser, int idSala) {
            this.idUser = idUser;
            this.idSala = idSala;
            this.status = Status.IN_ASTEPTARE;
            this.timpIntrare = System.currentTimeMillis();
        }

        public int getIdUser() {
            return idUser;
        }

        public int getIdSala() {
            return idSala;
        }

        public synchronized Status getStatus() {
            return status;
        }

        public synchronized void setStatus(Status status) {
            this.status = status;
            if (status == Status.IN_SALA) {
                this.timpIntrare = System.currentTimeMillis();
            }
        }

        public synchronized long getTimpIntrare() {
            return timpIntrare;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "idUser=" + idUser +
                    ", idSala=" + idSala +
                    ", status=" + status +
                    ", timpIntrare=" + timpIntrare +
                    '}';
        }
    }

    static class Catalog {
        private final Map<Integer, Room> rooms;

        public Catalog(List<Room> roomList) {
            rooms = new HashMap<>();
            for (Room room : roomList) {
                rooms.put(room.getId(), room);
            }
        }

        public Room getRoom(int idSala) {
            return rooms.get(idSala);
        }

        public synchronized void printCatalog() {
            System.out.println("\n--- Starea Actuala a Salilor ---");
            for (Room room : rooms.values()) {
                System.out.println(room);
            }
        }
    }

    static class Register {
        private final List<Request> requests;

        public Register() {
            requests = new ArrayList<>();
        }

        public synchronized void addRequest(Request request) {
            requests.add(request);
        }

        public synchronized List<Request> getRequests() {
            return new ArrayList<>(requests);
        }

        public synchronized void updateRequestStatus(Request request, Status status) {
            request.setStatus(status);
        }

        public synchronized void removeWaitingRequests() {
            requests.removeIf(r -> r.getStatus() == Status.IN_ASTEPTARE);
        }

        public synchronized boolean allFinalized() {
            for (Request r : requests) {
                if (r.getStatus() != Status.FINALIZAT) {
                    return false;
                }
            }
            return true;
        }

        public synchronized void printRegister() {
            System.out.println("\n--- Starea Actuala a Registrului ---");
            for (Request r : requests) {
                System.out.println(r);
            }
        }
    }

    static class RequestQueue {
        private final LinkedList<Request> queue;
        private boolean isShutdown;

        public RequestQueue() {
            queue = new LinkedList<>();
            isShutdown = false;
        }

        public synchronized void enqueue(Request request) {
            if (!isShutdown) {
                queue.addLast(request);
                notifyAll();
            }
        }

        public synchronized Request dequeue() throws InterruptedException {
            while (queue.isEmpty() && !isShutdown) {
                wait();
            }
            if (isShutdown && queue.isEmpty()) {
                return null;
            }
            return queue.removeFirst();
        }

        public synchronized void clearQueue() {
            queue.clear();
            notifyAll();
        }

        public synchronized void shutdown() {
            isShutdown = true;
            notifyAll();
        }

        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    static class RequestGenerator implements Runnable {
        private static int userIdCounter = 1;
        private final Catalog catalog;
        private final Register register;
        private final RequestQueue queue;
        private final AtomicBoolean running;
        private final long endTime;
        private final Random rand;

        public RequestGenerator(Catalog catalog, Register register, RequestQueue queue, AtomicBoolean running, long endTime) {
            this.catalog = catalog;
            this.register = register;
            this.queue = queue;
            this.running = running;
            this.endTime = endTime;
            this.rand = new Random();
        }

        @Override
        public void run() {
            while (running.get() && System.currentTimeMillis() < endTime) {
                int idUser;
                synchronized (RequestGenerator.class) {
                    idUser = userIdCounter++;
                }
                int idSala = rand.nextInt(5) + 1;

                Request request = new Request(idUser, idSala);
                register.addRequest(request);
                queue.enqueue(request);

                System.out.println("Generata Cerere: " + request);

                try {
                    Thread.sleep(rand.nextInt(400) + 100);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("Thread Generator " + Thread.currentThread().getName() + " oprit.");
        }
    }

    static class Resolver implements Runnable {
        private final Catalog catalog;
        private final Register register;
        private final RequestQueue queue;
        private final AtomicBoolean running;

        public Resolver(Catalog catalog, Register register, RequestQueue queue, AtomicBoolean running) {
            this.catalog = catalog;
            this.register = register;
            this.queue = queue;
            this.running = running;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Request request = queue.dequeue();
                    if (request == null) {
                        break;
                    }

                    synchronized (request) {
                        if (request.getStatus() != Status.IN_ASTEPTARE) {
                            continue;
                        }
                        Room room = catalog.getRoom(request.getIdSala());
                        if (room == null) {
                            System.out.println("ID de sala invalid: " + request.getIdSala());
                            continue;
                        }

                        boolean entered = room.incrementOccupancy();
                        if (entered) {
                            register.updateRequestStatus(request, Status.IN_SALA);
                            System.out.println("Cerere " + request.getIdUser() + " a intrat in sala " + request.getIdSala());
                        } else {
                            queue.enqueue(request);
                            System.out.println("Nu exista locuri disponibile pentru Cererea " + request.getIdUser() + " in sala " + request.getIdSala() + ". Re-adaugata in coada.");
                            Thread.sleep(100);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("Thread Resolver " + Thread.currentThread().getName() + " oprit.");
        }
    }

    static class Administrator implements Runnable {
        private final Catalog catalog;
        private final Register register;
        private final RequestQueue queue;
        private final AtomicBoolean running;
        private final long Dt;

        public Administrator(Catalog catalog, Register register, RequestQueue queue, AtomicBoolean running, long Dt) {
            this.catalog = catalog;
            this.register = register;
            this.queue = queue;
            this.running = running;
            this.Dt = Dt;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            boolean dtExceeded = false;

            while (true) {
                long currentTime = System.currentTimeMillis();

                if (!dtExceeded && (currentTime - startTime >= Dt)) {
                    dtExceeded = true;
                    running.set(false);
                    queue.shutdown();
                    queue.clearQueue();
                    System.out.println("\nDt a fost depasit. Generarea de noi cereri a fost oprita si coada de asteptare a fost golita.");

                    List<Request> allRequests = register.getRequests();
                    for (Request request : allRequests) {
                        synchronized (request) {
                            if (request.getStatus() == Status.IN_ASTEPTARE) {
                                register.updateRequestStatus(request, Status.FINALIZAT);
                                System.out.println("Cererea " + request.getIdUser() + " a fost finalizata automat (IN_ASTEPTARE).");
                            }
                        }
                    }
                }

                List<Request> requests = register.getRequests();
                for (Request request : requests) {
                    if (request.getStatus() == Status.IN_SALA) {
                        Room room = catalog.getRoom(request.getIdSala());
                        if (room != null) {
                            long elapsed = currentTime - request.getTimpIntrare();
                            if (elapsed >= room.getTimpMaxim()) {
                                register.updateRequestStatus(request, Status.FINALIZAT);
                                room.decrementOccupancy();
                                System.out.println("Cererea " + request.getIdUser() + " din sala " + request.getIdSala() + " a fost finalizata.");
                            }
                        }
                    }
                }

                if (dtExceeded && register.allFinalized()) {
                    System.out.println("\nToate cererile au fost finalizate. Administratorul opreste sistemul.");
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }


    public static void main(String[] args) {
        List<Room> roomList = Arrays.asList(
                new Room(1, 5, 2000),
                new Room(2, 7, 5000),
                new Room(3, 10, 3000),
                new Room(4, 8, 7000),
                new Room(5, 12, 6000)
        );

        Catalog catalog = new Catalog(roomList);
        Register register = new Register();
        RequestQueue queue = new RequestQueue();
        AtomicBoolean running = new AtomicBoolean(true);
        long Dt = 7000;
        long endTime = System.currentTimeMillis() + Dt;

        int A = 4;
        List<Thread> generators = new ArrayList<>();
        for (int i = 0; i < A; i++) {
            Thread t = new Thread(new RequestGenerator(catalog, register, queue, running, endTime), "Generator-" + (i + 1));
            generators.add(t);
            t.start();
        }

        int B = 3;
        List<Thread> resolvers = new ArrayList<>();
        for (int i = 0; i < B; i++) {
            Thread t = new Thread(new Resolver(catalog, register, queue, running), "Resolver-" + (i + 1));
            resolvers.add(t);
            t.start();
        }

        Thread admin = new Thread(new Administrator(catalog, register, queue, running, Dt), "Administrator");
        admin.start();

        try {
            admin.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread t : generators) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread t : resolvers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        catalog.printCatalog();
        register.printRegister();

        System.out.println("\nSistemul s-a inchis complet.");
        System.exit(0);
    }
}