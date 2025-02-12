package org.example;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
//    private static void generateFiles() {
//        File directory = new File("files");
//        if (directory.exists()) {
//            for (int i = 0; i < 8; i++) {
//
//            }
//        }
//    }

    public static void main(String[] args) {

//        IOHandler.createRandomFiles();
//        System.out.println("Au fost generate fisierele cu muzica");
//        long startTime = System.currentTimeMillis();
//        IOHandler.cleanFile("fisier.log");

        IOHandler.cleanFile("fisier.log");

        IOHandler.cleanFile("fisier.log");
        int F = 8;
        int start = 0;
        int end;
        int batchSize = F / 4;
        int batchReminder = F % 4;
        AtomicInteger totalProducers = new AtomicInteger(4);
        AtomicInteger totalConsumers = new AtomicInteger(8);
        AtomicInteger messagesSent = new AtomicInteger(0);
        MyQueue<Message> queue = new MyQueue<>(totalProducers);
        MyMap1 map1 = new MyMap1();
        MyMap2 map2 = new MyMap2();
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        Thread supervisor;

        for (int i = 0; i < totalProducers.get(); i++) {
            end = start + batchSize;
            if (batchReminder > 0) {
                end++;
                batchReminder--;
            }
            producers.add(new Producer(queue, totalProducers, start, end, messagesSent));
            Thread lastProducer = producers.get(producers.size() - 1);
            lastProducer.start();
            start = end;
        }

        for (int i = 0; i < totalConsumers.get(); i++) {
            consumers.add(new Consumer(queue, totalConsumers, map1, map2));
            Thread lastConsumer = consumers.get(consumers.size() - 1);
            lastConsumer.start();
        }

        supervisor = new Supervisor(queue, map1, map2, totalProducers, totalConsumers, messagesSent);
        supervisor.start();

        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread consumer : consumers) {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            supervisor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static class Producer extends Thread {
        MyQueue<Message> queue;
        AtomicInteger totalProducers;
        int start;
        int end;
        AtomicInteger messageSent;

        public Producer(MyQueue<Message> queue, AtomicInteger totalProducers, int start, int end, AtomicInteger messageSent) {
            this.queue = queue;
            this.totalProducers = totalProducers;
            this.start = start;
            this.end = end;
            this.messageSent = messageSent;
        }

        @Override
        public void run() {
            try {
                for (int i = start; i < end; i++) {
                    String filename = "Muzica" + i + ".txt";
                    List<Message> messages = IOHandler.read(filename);
                    for (Message msg : messages) {
                        queue.push(msg);
                        //actualizam contorul de mesaje
                        messageSent.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (this) {
                totalProducers.decrementAndGet();
                System.out.println("Producer " + start + " - " + end + "finished");
                queue.finish();
            }
        }
    }

    public static class Consumer extends Thread {
        MyQueue<Message> queue;
        AtomicInteger totalConsumers;
        MyMap1 map1;
        MyMap2 map2;

        public Consumer(MyQueue<Message> queue, AtomicInteger totalConsumers, MyMap1 map1, MyMap2 map2) {
            this.queue = queue;
            this.totalConsumers = totalConsumers;
            this.map1 = map1;
            this.map2 = map2;
        }

        @Override
        public void run() {
            while (queue.hasData()) {
                try {
                    Message mesaj = queue.pop();
                    if (mesaj == null) {
                        continue;
                    }
                    map1.add(mesaj);
                    map2.add(mesaj);
                    sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            totalConsumers.decrementAndGet();
            System.out.println("Consumer finished");
        }
    }

    private static class Supervisor extends Thread {
        MyQueue<Message> queue;
        MyMap1 map1;
        MyMap2 map2;
        AtomicInteger totalProducers;
        AtomicInteger totalConsumers;
        AtomicInteger messagesSent;

        public Supervisor(MyQueue<Message> queue,
                          MyMap1 map1,
                          MyMap2 map2,
                          AtomicInteger totalProducers,
                          AtomicInteger totalConsumers,
                          AtomicInteger messagesSent) {
            this.queue = queue;
            this.map1 = map1;
            this.map2 = map2;
            this.totalProducers = totalProducers;
            this.totalConsumers = totalConsumers;
            this.messagesSent = messagesSent;
        }

        @Override
        public void run() {
            while (totalProducers.get() > 0 || totalConsumers.get() > 0) {
                try {
                    IOHandler.writeText(LocalDateTime.now() + ": " + messagesSent.get() + "=" + queue.size() + "+" + map1.size() + "/" + map2.size() + "\n");
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            IOHandler.writeText("DONE! " + LocalDateTime.now() + ": " + messagesSent.get() + "=" + queue.size() + "+" + map1.size() + "/" + map2.size() + "\n");
        }
    }
}



