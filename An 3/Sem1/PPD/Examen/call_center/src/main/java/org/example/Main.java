package org.example;

import javax.sound.midi.Soundbank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        IOHandler.clearLogFile();

        int numAgents = 5;
        int numCallsPerAgent = 100;
//        int agentInterval = 10;
        int agentInterval = 50;
        int numSpecialists = 6;
        int resolveTime = 5;
//        int supervisorInterval = 30;
        int supervisorInterval = 500;

        AtomicInteger totalProducers = new AtomicInteger(5);
        AtomicInteger totalConsumers = new AtomicInteger(6);
        MyQueue<Call> queue1 = new MyQueue<>(totalProducers);
        MyQueue<Call> queue2 = new MyQueue<>(totalProducers);
        MyQueue<Call> queue3 = new MyQueue<>(totalProducers);

        MyMap resolvedCalls = new MyMap();

        List<Thread> agents = new ArrayList<>();
        List<Thread> specialists = new ArrayList<>();

        for(int i = 0; i < numAgents; i++){
            agents.add(new Agent(i+1,  totalProducers, queue1, queue2, queue3, numCallsPerAgent, agentInterval));
            agents.get(agents.size() - 1).start();
        }
        for(int i = 0; i < numSpecialists; i++){
            MyQueue<Call> queue = new MyQueue<>(totalProducers);
            if(i < 2){
                queue = queue1;
            }
            else if( i < 4){
                queue = queue2;
            }
            else{
                queue = queue3;
            }
            specialists.add(new Specialist(i + 1, queue, resolvedCalls, resolveTime));
            specialists.get(specialists.size() - 1).start();
        }
        Thread supervisor = new Supervisor(queue1,totalProducers, queue2, queue3, resolvedCalls, supervisorInterval);
        supervisor.start();
        for (Thread agent : agents) {
            try {
                agent.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        for (Thread specialist : specialists) {
            try {
                specialist.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        supervisor.interrupt();
        try {
            supervisor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Procesarea apelurilor s-a incheiat");
        IOHandler.writeText("Procesarea apelurilor s-a încheiat la " + LocalDateTime.now());
    }

    public static class Agent extends Thread {
        private int idAgent;
        private MyQueue<Call> queue1;
        private MyQueue<Call> queue2;
        private MyQueue<Call> queue3;

        AtomicInteger totalProducers;
        private int numCalls;

        private int interval;

        public Agent(int idAgent, AtomicInteger totalProducers, MyQueue<Call> queue1, MyQueue<Call> queue2, MyQueue<Call> queue3, int numCalls, int interval) {
            this.idAgent = idAgent;
            this.totalProducers = totalProducers;
            this.queue1 = queue1;
            this.queue2 = queue2;
            this.queue3 = queue3;
            this.numCalls = numCalls;
            this.interval = interval;
        }

        @Override
        public void run() {
            for (int i = 0; i < numCalls; i++) {
                try {
                    int difficulty = (int) (Math.random() * 3) + 1;
                    Call call = new Call(idAgent, i + 1, difficulty);
                    if (difficulty == 1) {
                        queue1.push(call);
                    } else if (difficulty == 2) {
                        queue2.push(call);
                    } else {
                        queue3.push(call);
                    }
                    System.out.println("Agent " + idAgent + " a trimis apelul: " + call);
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            synchronized (this) {
                totalProducers.decrementAndGet();
                System.out.println("Producer " + idAgent + " finished");
                queue1.finish();
                queue2.finish();
                queue3.finish();

            }
        }
    }

    public static class Specialist extends Thread {
        private int idSpecialist;
        private MyQueue<Call> queue;
        private MyMap resolvedCalls;
        private int resolveTime;

        public Specialist(int idSpecialist, MyQueue<Call> queue, MyMap resolvedCalls, int resolveTime) {
            this.idSpecialist = idSpecialist;
            this.queue = queue;
            this.resolvedCalls = resolvedCalls;
            this.resolveTime = resolveTime;
        }

        @Override
        public void run() {
            while (queue.hasData()) {
                try {
                    Call call = queue.pop();
                    if (call == null) {
                        continue;
                    }
                    resolvedCalls.add(call);
                    System.out.println("Specialist " + idSpecialist + " a rezolvat apelul" + call);
                    sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Supervisor extends Thread {
        private MyQueue<Call> queue1;
        private MyQueue<Call> queue2;
        private MyQueue<Call> queue3;
        private MyMap resolvedCalls;
        private int interval;

        private AtomicInteger totalProducers;
        public Supervisor(MyQueue<Call> queue1, AtomicInteger totalProducers, MyQueue<Call> queue2, MyQueue<Call> queue3, MyMap resolvedCalls, int interval) {
            this.queue1 = queue1;
            this.queue2 = queue2;
            this.queue3 = queue3;
            this.totalProducers = totalProducers;
            this.resolvedCalls = resolvedCalls;
            this.interval = interval;

        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int queue1Size = queue1.size();
                    int queue2Size = queue2.size();
                    int queue3Size = queue3.size();

                    if (queue1Size == 0 && queue2Size == 0 && queue3Size == 0 && totalProducers.get() == 0) {
                        System.out.println("Supervisor: Toate cozile sunt goale, toti producatorii au terminat.");
                        break;
                    }

                    int totalResolvedCalls = resolvedCalls.size();

                    StringBuilder report = new StringBuilder();
                    report.append("Timestamp: ").append(LocalDateTime.now()).append("\n");
                    report.append("Queue 1 (dificultate 1) size: ").append(queue1Size).append("\n");
                    report.append("Queue 2 (dificultate 2) size: ").append(queue2Size).append("\n");
                    report.append("Queue 3 (dificultate 3) size: ").append(queue3Size).append("\n");
                    report.append("Total apeluri rezolvate: ").append(totalResolvedCalls).append("\n");

                    Map<Integer, List<Call>> resolvedMap = resolvedCalls.getMap();
                    for (Map.Entry<Integer, List<Call>> entry : resolvedMap.entrySet()) {
                        int agentId = entry.getKey();
                        int resolvedCount = entry.getValue().size();
                        report.append("Agent ").append(agentId).append(": ").append(resolvedCount).append(" apeluri rezolvate.\n");
                    }

                    IOHandler.writeText(report.toString());
                    System.out.println(report);

                    Thread.sleep(interval);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("Supervisor finished");
        }

    }
}