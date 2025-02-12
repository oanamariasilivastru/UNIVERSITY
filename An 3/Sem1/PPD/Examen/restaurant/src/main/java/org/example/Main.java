package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int C = 5;
        int Yc = 50;
        long Tc = 170;
        int B = 2;
        long Xb = 150;
        long Rn = 200;
        long Dt = 7000;

        long startTime = System.currentTimeMillis();
        long deadline = startTime + Dt;
        OrderRegister orderRegister = new OrderRegister();


        NotificationThread notification = new NotificationThread(orderRegister, Rn, deadline);
        notification.start();

        List<Thread> clientThreads = new ArrayList<>();
        for (int i = 1; i <= C; i++) {
            ClientThread client = new ClientThread(i, Yc, Tc, deadline, orderRegister);
            clientThreads.add(client);
            client.start();
        }

        List<ReceptionistThread> receptionists = new ArrayList<>();
        for (int i = 1; i <= B; i++) {
            ReceptionistThread r = new ReceptionistThread(orderRegister);
            receptionists.add(r);
            r.start();
        }

        KitchenThread sushiTeam = new KitchenThread("A1-Sushi", BlockingQueues.QaSushi, orderRegister, Xb);
        KitchenThread pizzaTeam = new KitchenThread("A2-Pizza", BlockingQueues.QaPizza, orderRegister, Xb);
        KitchenThread pastaTeam = new KitchenThread("A3-Paste", BlockingQueues.QaPaste, orderRegister, Xb);
        sushiTeam.start();
        pizzaTeam.start();
        pastaTeam.start();

        Thread.sleep(Dt);
        notification.stopNotification();

        for (Thread c : clientThreads) {
            c.interrupt();
        }

        for (ReceptionistThread r : receptionists) {
            r.stopReception();
        }

        for (Thread c : clientThreads) {
            c.join();
        }
        for (ReceptionistThread r : receptionists) {
            r.join();
        }

        sushiTeam.interrupt();
        pizzaTeam.interrupt();
        pastaTeam.interrupt();
        sushiTeam.join();
        pizzaTeam.join();
        pastaTeam.join();

        notification.join();

        System.out.println("=== Restaurantul s-a inchis. Toate thread-urile s-au oprit. ===");
    }

    public static class ClientThread extends Thread {
        private final int clientId;
        private final int numberOfOrders;
        private final long interval;
        private final long deadline;
        private final OrderRegister orderRegister;

        public ClientThread(int clientId, int numberOfOrders, long interval, long deadline,
                            OrderRegister orderRegister) {
            this.clientId = clientId;
            this.numberOfOrders = numberOfOrders;
            this.interval = interval;
            this.deadline = deadline;
            this.orderRegister = orderRegister;
        }

        @Override
        public void run() {
            for (int i = 1; i <= numberOfOrders; i++) {
                try {

                    if (System.currentTimeMillis() > deadline) {
                        System.out.println("Client " + clientId + " nu mai genereaza comenzi (s-a depasit Dt).");
                        break;
                    }

                    String foodType = getRandomFoodType();
                    Order order = new Order(clientId * 1000 + i, foodType);

                    BlockingQueues.Qc.put(order);
                    System.out.println("Client " + clientId + " a generat " + order);

                    Thread.sleep(interval);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("Client " + clientId + " a terminat generarea de comenzi.");
        }

        private String getRandomFoodType() {
            double r = Math.random();
            if (r < 0.33) return "sushi";
            else if (r < 0.66) return "pizza";
            else return "paste";
        }
    }

    public static class ReceptionistThread extends Thread {
        private final OrderRegister orderRegister;
        private volatile boolean running = true;

        public ReceptionistThread(OrderRegister orderRegister) {
            this.orderRegister = orderRegister;
        }

        public void stopReception() {
            running = false;
            this.interrupt();
        }

        @Override
        public void run() {
            while (running || !BlockingQueues.Qc.isEmpty()) {
                try {
                    Order order = BlockingQueues.Qc.poll();
                    if (order == null) {
                        Thread.sleep(50);
                        continue;
                    }
                    orderRegister.addOrder(order.getIdOrder());
                    switch (order.getFoodType()) {
                        case "sushi" -> BlockingQueues.QaSushi.put(order);
                        case "pizza" -> BlockingQueues.QaPizza.put(order);
                        case "paste" -> BlockingQueues.QaPaste.put(order);
                        default -> System.out.println("Comanda cu tip necunoscut: " + order);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("Receptionist " + getName() + " s-a oprit.");
        }
    }

    public static  class KitchenThread extends Thread {
        private final String teamName;
        private final BlockingQueue<Order> kitchenQueue;
        private final OrderRegister orderRegister;
        private final long cookingTime;

        public KitchenThread(String teamName,
                             BlockingQueue<Order> queue,
                             OrderRegister register,
                             long cookingTime) {
            this.teamName = teamName;
            this.kitchenQueue = queue;
            this.orderRegister = register;
            this.cookingTime = cookingTime;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Order order = kitchenQueue.take();
                    orderRegister.setStatus(order.getIdOrder(), OrderStatus.IN_PROCESARE);

                    Thread.sleep(cookingTime);

                    orderRegister.setStatus(order.getIdOrder(), OrderStatus.FINALIZATA);

                    System.out.println(teamName + " a finalizat comanda: " + order.getIdOrder());

                } catch (InterruptedException e) {
                    System.out.println(teamName + " termina lucrul.");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public static class NotificationThread extends Thread {
        private final OrderRegister orderRegister;
        private final long reportInterval;
        private final long deadline;
        private volatile boolean running = true;

        public NotificationThread(OrderRegister register, long reportInterval, long deadline) {
            this.orderRegister = register;
            this.reportInterval = reportInterval;
            this.deadline = deadline;
        }

        public void stopNotification() {
            running = false;
            this.interrupt();
        }

        @Override
        public void run() {
            while (running) {
                try {
                    long now = System.currentTimeMillis();
                    if (now >= deadline) {
                        System.out.println("=== A expirat durata de activitate a restaurantului (Dt) ===");
                        running = false;
                        break;
                    }

                    int inAsteptare = orderRegister.countByStatus(OrderStatus.IN_ASTEPTARE);
                    int inProcesare = orderRegister.countByStatus(OrderStatus.IN_PROCESARE);
                    int finalizate = orderRegister.countByStatus(OrderStatus.FINALIZATA);

                    String msg = String.format("[%tT] Comenzi: in asteptare=%d, in procesare=%d, finalizate=%d",
                            System.currentTimeMillis(), inAsteptare, inProcesare, finalizate);
//                    System.out.println(msg);
                    IOHandler.writeText(msg);

                    Thread.sleep(reportInterval);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("NotificationThread s-a oprit.");
        }
    }





}