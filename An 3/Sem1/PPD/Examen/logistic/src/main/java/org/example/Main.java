package org.example;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final MyDictionary<Integer, List<Integer>> completedCommands = new MyDictionary<>();

    public static void main(String[] args) {
        IOHandler.createInputFile(90);

        AtomicInteger totalProducers = new AtomicInteger(3);
        MyQueue<Structura> Qp1 = new MyQueue<>(totalProducers);
        MyQueue<Structura> Qp2 = new MyQueue<>(totalProducers);

        List<Comanda> allCommands = IOHandler.read("input.txt");
        Iterator<Comanda> commandIterator = allCommands.iterator();

        Object readLock = new Object();

        int numberOfOperators = 3;
        int commandsPerBatch = 10;
        int intervalToMs = 15;

        int numberOfPackagingTeams = 4;
        int packagingTimeMs = 10;

        int managerIntervalMs = 40;

        AtomicInteger activeConsumers = new AtomicInteger(numberOfPackagingTeams);

        List<Thread> producers = new ArrayList<>();
        for (int i = 1; i <= numberOfOperators; i++) {
            Agent producer = new Agent(
                    i,
                    Qp1,
                    Qp2,
                    commandsPerBatch,
                    intervalToMs,
                    commandIterator,
                    readLock,
                    totalProducers
            );
            producers.add(producer);
            producer.start();
            System.out.println("Started Operator " + i);
        }

        List<Thread> consumers = new ArrayList<>();
        for (int i = 1; i <= numberOfPackagingTeams; i++) {
            Consumer consumer = new Consumer(
                    i,
                    Qp1,
                    Qp2,
                    packagingTimeMs,
                    completedCommands,
                    totalProducers,
                    activeConsumers
            );
            consumers.add(consumer);
            consumer.start();
            System.out.println("Started Packaging Team " + i);
        }

        Manager manager = new Manager(
                Qp1,
                Qp2,
                managerIntervalMs,
                totalProducers
        );
        manager.start();
        System.out.println("Started Manager");

        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted while waiting for Producers.");
            }
        }

        for (Thread consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted while waiting for Consumers.");
            }
        }

        manager.interrupt();
        try {
            manager.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted while waiting for Manager.");
        }

        StringBuilder report = new StringBuilder();
        List<MyDictionary.Entry<Integer, List<Integer>>> entries = completedCommands.getAllEntries();
        for (MyDictionary.Entry<Integer, List<Integer>> entry : entries) {
            report.append("Operator ").append(entry.getKey()).append(": ");
            report.append(entry.getValue()).append("\n");
        }
        IOHandler.write("src/main/resources/outputs/output.txt", report.toString());
        System.out.println("Final report has been written to 'output.txt'.");
    }

    public static class Agent extends Thread {
        private final int idOperator;
        private final MyQueue<Structura> Qp1;
        private final MyQueue<Structura> Qp2;
        private final int commandsPerBatch;
        private final int intervalToMs;
        private final Iterator<Comanda> commandIterator;
        private final Object readLock;
        private final AtomicInteger activeProducers;

        public Agent(int idOperator,
                     MyQueue<Structura> Qp1,
                     MyQueue<Structura> Qp2,
                     int commandsPerBatch,
                     int intervalToMs,
                     Iterator<Comanda> commandIterator,
                     Object readLock,
                     AtomicInteger activeProducers) {
            this.idOperator = idOperator;
            this.Qp1 = Qp1;
            this.Qp2 = Qp2;
            this.commandsPerBatch = commandsPerBatch;
            this.intervalToMs = intervalToMs;
            this.commandIterator = commandIterator;
            this.readLock = readLock;
            this.activeProducers = activeProducers;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    List<Comanda> batch = new ArrayList<>();
                    synchronized (readLock) {
                        for (int i = 0; i < commandsPerBatch && commandIterator.hasNext(); i++) {
                            Comanda comanda = commandIterator.next();
                            batch.add(comanda);
                        }
                    }

                    if (batch.isEmpty()) {
                        break;
                    }

                    for (Comanda comanda : batch) {
                        Structura structura = new Structura(idOperator, comanda.getIdComanda());
                        if (comanda.getPrioritate() == 1) {
                            Qp1.push(structura);
                            System.out.println("Operator " + idOperator + " enqueued to Qp1: " + structura);
                        } else if (comanda.getPrioritate() == 2) {
                            Qp2.push(structura);
                            System.out.println("Operator " + idOperator + " enqueued to Qp2: " + structura);
                        } else {
                            System.err.println("Invalid priority for command: " + comanda);
                        }
                    }

                    Thread.sleep(intervalToMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Operator " + idOperator + " interrupted.");
            } catch (Exception e) {
                System.err.println("Operator " + idOperator + " encountered an error: " + e.getMessage());
                e.printStackTrace();
            } finally {

                activeProducers.decrementAndGet();
                System.out.println("Operator " + idOperator + " has finished processing. Active Producers: " + activeProducers.get());

                if (activeProducers.get() == 0) {
                    Qp1.finish();
                    Qp2.finish();
                    System.out.println("All operators have finished. Queues are marked as finished.");
                }
            }
        }
    }

    public static class Consumer extends Thread {
        private final int idConsumer;
        private final MyQueue<Structura> Qp1;
        private final MyQueue<Structura> Qp2;
        private final int packagingTimeMs;
        private final MyDictionary<Integer, List<Integer>> completedCommands;
        private final AtomicInteger activeProducers;
        private final AtomicInteger activeConsumers;

        public Consumer(int idConsumer,
                        MyQueue<Structura> Qp1,
                        MyQueue<Structura> Qp2,
                        int packagingTimeMs,
                        MyDictionary<Integer, List<Integer>> completedCommands,
                        AtomicInteger activeProducers,
                        AtomicInteger activeConsumers) {
            this.idConsumer = idConsumer;
            this.Qp1 = Qp1;
            this.Qp2 = Qp2;
            this.packagingTimeMs = packagingTimeMs;
            this.completedCommands = completedCommands;
            this.activeProducers = activeProducers;
            this.activeConsumers = activeConsumers;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Structura structura = Qp2.pop();
                    if (structura == null) {
                        structura = Qp1.pop();
                        if (structura == null) {
                            System.out.println("Packaging Team " + idConsumer + " detected no more commands. Exiting.");
                            break;
                        }
                        System.out.println("Packaging Team " + idConsumer + " dequeued from Qp1: " + structura);
                    } else {
                        System.out.println("Packaging Team " + idConsumer + " dequeued from Qp2: " + structura);
                    }

                    Thread.sleep(packagingTimeMs);


                    synchronized (structura) {
                        List<Integer> commands = completedCommands.get(structura.getIdOperator());
                        if (commands == null) {
                            commands = new ArrayList<>();
                            completedCommands.put(structura.getIdOperator(), commands);
                        }
                        commands.add(structura.getIdComanda());
                    }
                    System.out.println("Packaging Team " + idConsumer + " processed: " + structura);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Packaging Team " + idConsumer + " interrupted.");
            } catch (Exception e) {
                System.err.println("Packaging Team " + idConsumer + " encountered an error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                activeConsumers.decrementAndGet();
                System.out.println("Packaging Team " + idConsumer + " has finished processing. Active Consumers: " + activeConsumers.get());
            }
        }
    }

    public static class Manager extends Thread {
        private final MyQueue<Structura> Qp1;
        private final MyQueue<Structura> Qp2;
        private final int managerIntervalMs;
        private final AtomicInteger activeProducers;

        public Manager(MyQueue<Structura> Qp1,
                       MyQueue<Structura> Qp2,
                       int managerIntervalMs,
                       AtomicInteger activeProducers) {
            this.Qp1 = Qp1;
            this.Qp2 = Qp2;
            this.managerIntervalMs = managerIntervalMs;
            this.activeProducers = activeProducers;
        }

        @Override
        public void run() {
            try {
                while (activeProducers.get() > 0 || Qp1.size() > 0 || Qp2.size() > 0) {
                    String logEntry = LocalDateTime.now() + ": Qp1 size = " + Qp1.size() +
                            ", Qp2 size = " + Qp2.size();
                    System.out.println(logEntry);
                    IOHandler.write("src/main/resources/outputs/output.txt", logEntry + "\n");
                    Thread.sleep(managerIntervalMs);
                }
            } catch (InterruptedException e) {

                System.out.println("Manager thread interrupted. Finalizing logs.");
            } catch (Exception e) {
                System.err.println("Manager encountered an error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                String finalLog = LocalDateTime.now() + ": All commands have been processed.";
                System.out.println(finalLog);
                IOHandler.write("src/main/resources/outputs/output.txt", finalLog + "\n");
            }
        }
    }
}
