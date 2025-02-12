package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

/**
 * Main class care initializeaza si gestioneaza thread-urile pentru procesarea pacientilor.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        IOHandler.clearLogFile();

        final int TOTAL_PATIENTS = 200;
        final int ASSISTANTS_COUNT = 2;
        final int DISTRIBUTORS_COUNT = 3;
        final int SERVICES_COUNT = 4;

        // Fiecare asistent va gestiona jumatate din pacienti (pentru 2 asistenti)
        int patientsPerAssistant = TOTAL_PATIENTS / ASSISTANTS_COUNT;

        // Timpurile de consultatie (in ms)
        long t_oft = 20;  // Serviciul 1: oftalmologie
        long t_r   = 10;  // Serviciul 2: radiografie
        long t_ort = 40;  // Serviciul 3: ortopedie
        long t_orl = 25;  // Serviciul 4: ORL

        /*
          ===================================================
          Configurarea cozilor si a contorilor de producatori
          ===================================================
          - Coada globala este produsa doar de asistenti,
            deci totalProducers = numarul de asistenti (2).
          - Fiecare coada de serviciu i este produsa de:
              * Cei 3 distribuitori (deoarece orice distribuitor poate adauga in coada i)
              * Ceilalti 3 thread-uri de servicii (NU thread-ul de serviciu i).
         */

        // Coada globala + contorul ei de producatori
        AtomicInteger globalProducers = new AtomicInteger(ASSISTANTS_COUNT);
        MyQueue<Ticket> globalQueue = new MyQueue<>(globalProducers);

        // Pentru fiecare dintre cele 4 cozi de servicii: 3 distribuitori + 3 alte servicii = 6
        AtomicInteger q1Producers = new AtomicInteger(DISTRIBUTORS_COUNT + (SERVICES_COUNT - 1)); // 3 + 3 = 6
        MyQueue<Ticket> queue1 = new MyQueue<>(q1Producers);

        AtomicInteger q2Producers = new AtomicInteger(DISTRIBUTORS_COUNT + (SERVICES_COUNT - 1));
        MyQueue<Ticket> queue2 = new MyQueue<>(q2Producers);

        AtomicInteger q3Producers = new AtomicInteger(DISTRIBUTORS_COUNT + (SERVICES_COUNT - 1));
        MyQueue<Ticket> queue3 = new MyQueue<>(q3Producers);

        AtomicInteger q4Producers = new AtomicInteger(DISTRIBUTORS_COUNT + (SERVICES_COUNT - 1));
        MyQueue<Ticket> queue4 = new MyQueue<>(q4Producers);

        // Creeaza si porneste thread-urile de asistenti
        Assistant a1 = new Assistant(globalProducers, 1, patientsPerAssistant, globalQueue);
        Assistant a2 = new Assistant(globalProducers, 2, patientsPerAssistant, globalQueue);

        // Creeaza si porneste thread-urile de distribuitori
        Distributor d1 = new Distributor(1, globalQueue, queue1, queue2, queue3, queue4);
        Distributor d2 = new Distributor(2, globalQueue, queue1, queue2, queue3, queue4);
        Distributor d3 = new Distributor(3, globalQueue, queue1, queue2, queue3, queue4);

        // Creeaza si porneste thread-urile de servicii
        // Fiecare serviciu consuma din propria coada, dar poate produce in celelalte trei.
        ServiceThread sOft = new ServiceThread(1, t_oft, queue1, queue2, queue3, queue4);
        ServiceThread sRad = new ServiceThread(2, t_r,   queue2, queue1, queue3, queue4);
        ServiceThread sOrt = new ServiceThread(3, t_ort, queue3, queue1, queue2, queue4);
        ServiceThread sOrl = new ServiceThread(4, t_orl, queue4, queue1, queue2, queue3);

        // Creeaza si porneste thread-ul Admin
        AdminThread admin = new AdminThread(globalQueue, queue1, queue2, queue3, queue4, 200);

        // Pornire efectiva a thread-urilor
        a1.start();
        a2.start();
        d1.start();
        d2.start();
        d3.start();
        sOft.start();
        sRad.start();
        sOrt.start();
        sOrl.start();
        admin.start();

        // Asteapta finalizarea thread-urilor de asistenti
        a1.join();
        a2.join();
        System.out.println("Asistentii au terminat de produs bilete.");

        // Asteapta finalizarea thread-urilor de distribuitori
        d1.join();
        d2.join();
        d3.join();
        System.out.println("Distribuitorii au terminat de consumat coada globala.");

        // Asteapta finalizarea thread-urilor de servicii
        sOft.join();
        sRad.join();
        sOrt.join();
        sOrl.join();
        System.out.println("Toate serviciile au finalizat consultatiile.");

        // Asteapta finalizarea thread-ului Admin
        admin.join();
        System.out.println("AdminThread s-a oprit.");
        System.out.println("Ziua de lucru s-a incheiat. Verificati clinic_log.txt pentru istoricul complet.");
    }

    // --------------------------------------------------------------------------------------------
    // MODEL: Ticket
    // --------------------------------------------------------------------------------------------
    public static class Ticket {
        private final int id;
        private final int service1;
        private final int service2;

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
            return "Ticket{id=" + id + ", s1=" + service1 + ", s2=" + service2 + "}";
        }
    }

    // --------------------------------------------------------------------------------------------
    // UTILITY: IOHandler pentru logging
    // --------------------------------------------------------------------------------------------
    public static class IOHandler {
        private static final String LOG_FILE = "clinic_log.txt";

        /**
         * Scrie text in fisierul de log intr-un mod thread-safe.
         */
        public static synchronized void writeText(String text) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                bw.write(text);
                bw.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Eroare la scrierea in fisierul log: " + e.getMessage());
            }
        }

        /**
         * Goleste fisierul de log.
         */
        public static synchronized void clearLogFile() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE))) {
                bw.write("");
            } catch (IOException e) {
                throw new RuntimeException("Eroare la golirea fisierului log: " + e.getMessage());
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // THREAD: Assistant (Producator pentru coada globala)
    // --------------------------------------------------------------------------------------------
    public static class Assistant extends Thread {
        private final int idAssistant;
        private final int numberOfPatients;
        private final AtomicInteger globalProducers;
        private final MyQueue<Ticket> globalQueue;

        public Assistant(AtomicInteger globalProducers, int idAssistant, int numberOfPatients, MyQueue<Ticket> globalQueue) {
            this.idAssistant = idAssistant;
            this.numberOfPatients = numberOfPatients;
            this.globalQueue = globalQueue;
            this.globalProducers = globalProducers;
        }

        @Override
        public void run() {
            for (int i = 1; i <= numberOfPatients; i++) {
                try {
                    int idPacient = idAssistant * 1000 + i;
                    // Genereaza aleator primul serviciu (1..4)
                    int s1 = 1 + (int) (Math.random() * 4);
                    // Genereaza aleator al doilea serviciu (0 sau 1..4)
                    int s2 = (Math.random() < 0.5) ? 0 : (1 + (int) (Math.random() * 4));

                    Ticket t = new Ticket(idPacient, s1, s2);
                    globalQueue.push(t);

                    System.out.println("Assistant " + idAssistant + " a adaugat " + t + " in coada globala.");
                    Thread.sleep(5); // Simuleaza timpul de productie
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            // Dupa terminarea producerii, decrementeaza contorul si semnaleaza consumatorii
            int remaining = globalProducers.decrementAndGet();
            System.out.println("Assistant " + idAssistant + " a terminat de produs. Remaining global producers: " + remaining);
            globalQueue.finish();
        }
    }

    // --------------------------------------------------------------------------------------------
    // THREAD: Distributor (Consumator din coada globala, producator pentru cozile de servicii)
    // --------------------------------------------------------------------------------------------
    public static class Distributor extends Thread {
        private final int idDistributor;
        private final MyQueue<Ticket> globalQueue;
        private final MyQueue<Ticket> q1;
        private final MyQueue<Ticket> q2;
        private final MyQueue<Ticket> q3;
        private final MyQueue<Ticket> q4;

        public Distributor(int idDistributor,
                           MyQueue<Ticket> globalQueue,
                           MyQueue<Ticket> q1,
                           MyQueue<Ticket> q2,
                           MyQueue<Ticket> q3,
                           MyQueue<Ticket> q4) {
            this.idDistributor = idDistributor;
            this.globalQueue = globalQueue;
            this.q1 = q1;
            this.q2 = q2;
            this.q3 = q3;
            this.q4 = q4;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Ticket t = globalQueue.pop();
                    if (t == null) {
                        break;  // Coada globala e goala si nu mai exista producatori
                    }
                    // In functie de service1, adauga in coada corespunzatoare
                    switch (t.getService1()) {
                        case 1 -> q1.push(t);
                        case 2 -> q2.push(t);
                        case 3 -> q3.push(t);
                        case 4 -> q4.push(t);
                        default -> System.out.println("Eroare: service1 invalid " + t);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            // Dupa terminarea consumului, scade contorii producatorilor pentru toate cozile
            q1.getTotalProducers().decrementAndGet();
            q2.getTotalProducers().decrementAndGet();
            q3.getTotalProducers().decrementAndGet();
            q4.getTotalProducers().decrementAndGet();

            q1.finish();
            q2.finish();
            q3.finish();
            q4.finish();

            System.out.println("Distributor " + idDistributor + " a terminat de produs.");
            System.out.println("Producer counts after Distributor " + idDistributor + " finished:");
            System.out.println("q1Producers: " + q1.getTotalProducers().get());
            System.out.println("q2Producers: " + q2.getTotalProducers().get());
            System.out.println("q3Producers: " + q3.getTotalProducers().get());
            System.out.println("q4Producers: " + q4.getTotalProducers().get());
        }
    }

    // --------------------------------------------------------------------------------------------
    // THREAD: ServiceThread (Consumator din propria coada, producator pentru celelalte cozile)
    // --------------------------------------------------------------------------------------------
    public static class ServiceThread extends Thread {
        private final int serviceId;
        private final long consultTime;
        private final MyQueue<Ticket> q1, q2, q3, q4;

        public ServiceThread(int serviceId,
                             long consultTime,
                             MyQueue<Ticket> q1,
                             MyQueue<Ticket> q2,
                             MyQueue<Ticket> q3,
                             MyQueue<Ticket> q4) {
            this.serviceId = serviceId;
            this.consultTime = consultTime;
            this.q1 = q1;
            this.q2 = q2;
            this.q3 = q3;
            this.q4 = q4;
        }

        @Override
        public void run() {
            // Determinam din ce coada sa consumam in functie de serviceId
            MyQueue<Ticket> serviceQueue;
            switch (serviceId) {
                case 1:
                    serviceQueue = q1;
                    break;
                case 2:
                    serviceQueue = q2;
                    break;
                case 3:
                    serviceQueue = q3;
                    break;
                case 4:
                    serviceQueue = q4;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid serviceId: " + serviceId);
            }

            while (serviceQueue.hasData()) {
                try {
                    Ticket t = serviceQueue.pop();
                    if (t == null) {
                        break;  // Coada este goala si nu mai exista producatori
                    }
                    Thread.sleep(consultTime); // Simuleaza timpul de consultatie

                    String msg = "Serviciu " + serviceId + " a procesat pacient " + t.getId()
                            + " (s1=" + t.getService1() + ", s2=" + t.getService2() + ")";
                    IOHandler.writeText(msg);
                    System.out.println(msg);

                    // Daca exista un al doilea serviciu solicitat si este diferit de serviciul curent
                    int s2 = t.getService2();
                    if (s2 != 0 && s2 != serviceId) {
                        switch (s2) {
                            case 1 -> q1.push(t);
                            case 2 -> q2.push(t);
                            case 3 -> q3.push(t);
                            case 4 -> q4.push(t);
                            default -> System.out.println("Eroare: service2 invalid la " + t);
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Dupa ce s-a terminat consumul, scade contorii producatorilor pentru toate cozile la care poate produce
            switch (serviceId) {
                case 1 -> {
                    q2.getTotalProducers().decrementAndGet();
                    q3.getTotalProducers().decrementAndGet();
                    q4.getTotalProducers().decrementAndGet();
                }
                case 2 -> {
                    q1.getTotalProducers().decrementAndGet();
                    q3.getTotalProducers().decrementAndGet();
                    q4.getTotalProducers().decrementAndGet();
                }
                case 3 -> {
                    q1.getTotalProducers().decrementAndGet();
                    q2.getTotalProducers().decrementAndGet();
                    q4.getTotalProducers().decrementAndGet();
                }
                case 4 -> {
                    q1.getTotalProducers().decrementAndGet();
                    q2.getTotalProducers().decrementAndGet();
                    q3.getTotalProducers().decrementAndGet();
                }
                default -> System.out.println("Eroare: serviceId invalid " + serviceId);
            }

            System.out.println("Serviciu " + serviceId + " a terminat de produs.");
            System.out.println("Producer counts after Serviciu " + serviceId + " finished:");
            System.out.println("q1Producers: " + q1.getTotalProducers().get());
            System.out.println("q2Producers: " + q2.getTotalProducers().get());
            System.out.println("q3Producers: " + q3.getTotalProducers().get());
            System.out.println("q4Producers: " + q4.getTotalProducers().get());

            // Semnaleaza faptul ca acest thread nu va mai pune nimic in propria coada
            serviceQueue.finish();
        }
    }

    // --------------------------------------------------------------------------------------------
    // THREAD: AdminThread (Monitorizeaza periodic starea cozilor)
    // --------------------------------------------------------------------------------------------
    public static class AdminThread extends Thread {
        private final MyQueue<Ticket> globalQ;
        private final MyQueue<Ticket> q1, q2, q3, q4;
        private final long interval;

        public AdminThread(MyQueue<Ticket> globalQ,
                           MyQueue<Ticket> q1,
                           MyQueue<Ticket> q2,
                           MyQueue<Ticket> q3,
                           MyQueue<Ticket> q4,
                           long interval) {
            this.globalQ = globalQ;
            this.q1 = q1;
            this.q2 = q2;
            this.q3 = q3;
            this.q4 = q4;
            this.interval = interval;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // Masuram dimensiunile cozilor
                    int gsize = globalQ.size();
                    int s1 = q1.size();
                    int s2 = q2.size();
                    int s3 = q3.size();
                    int s4 = q4.size();

                    String now = LocalTime.now().toString();
                    String line = String.format("Timp=%s | global=%d, q1=%d, q2=%d, q3=%d, q4=%d",
                            now, gsize, s1, s2, s3, s4);

                    IOHandler.writeText("[ADMIN] " + line);
                    System.out.println("[ADMIN] " + line);

                    // Verifica daca toate cozile sunt goale si nu mai exista producatori
                    boolean allEmpty = (gsize == 0 && s1 == 0 && s2 == 0 && s3 == 0 && s4 == 0);
                    boolean noProducers = (globalQ.getTotalProducers().get() == 0
                            && q1.getTotalProducers().get() == 0
                            && q2.getTotalProducers().get() == 0
                            && q3.getTotalProducers().get() == 0
                            && q4.getTotalProducers().get() == 0);

                    System.out.println("[ADMIN] Producer counts: q1=" + q1.getTotalProducers().get()
                            + ", q2=" + q2.getTotalProducers().get()
                            + ", q3=" + q3.getTotalProducers().get()
                            + ", q4=" + q4.getTotalProducers().get());

                    if (allEmpty && noProducers) {
                        System.out.println("[ADMIN] Toate cozile sunt goale si nu mai exista producatori. AdminThread se opreste.");
                        break;  // Ziua de lucru s-a incheiat
                    }

                    Thread.sleep(interval);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("AdminThread s-a oprit.");
        }
    }

    // --------------------------------------------------------------------------------------------
    // CLASA: MyQueue (Coada generica thread-safe cu semantici de "producer-consumer")
    // --------------------------------------------------------------------------------------------
    public static class MyQueue<T> {
        private final int MAX = 500;  // Capacitatea maxima (practic "suficient de mare")
        private final Lock lock;
        private final Condition notFull;
        private final Condition notEmpty;

        private final AtomicInteger totalProducers;
        private final Queue<T> queue = new LinkedList<>();

        public MyQueue(AtomicInteger totalProducers) {
            this.lock = new ReentrantLock();
            this.notFull = lock.newCondition();
            this.notEmpty = lock.newCondition();
            this.totalProducers = totalProducers;
        }

        public AtomicInteger getTotalProducers() {
            return totalProducers;
        }

        /**
         * Semnaleaza ca un thread a terminat productia pentru aceasta coada.
         * Se semnaleaza toate conditiile pentru a permite thread-urilor in asteptare sa reevalueze.
         */
        public void finish() {
            lock.lock();
            try {
                notEmpty.signalAll();
                notFull.signalAll();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Adauga un element in coada, asteptand daca este plina.
         */
        public void push(T element) throws InterruptedException {
            lock.lock();
            try {
                while (queue.size() == MAX) {
                    notFull.await();
                }
                queue.add(element);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Scoate un element din coada, asteptand daca este goala DAR mai exista producatori.
         * Daca nu mai exista producatori si coada este goala, returneaza null.
         */
        public T pop() throws InterruptedException {
            lock.lock();
            try {
                while (queue.isEmpty()) {
                    // Daca nu mai exista producatori pentru aceasta coada, nu mai are rost sa asteptam
                    if (totalProducers.get() == 0) {
                        return null;
                    }
                    notEmpty.await();
                }
                T result = queue.remove();
                notFull.signal();
                return result;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Verifica daca "mai avem date de consumat": coada nu e goala SAU inca exista producatori.
         */
        public boolean hasData() {
            lock.lock();
            try {
                if (totalProducers.get() == 0) {
                    return !queue.isEmpty();
                }
                return true;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Returneaza dimensiunea curenta a cozii, intr-un mod thread-safe.
         */
        public int size() {
            lock.lock();
            try {
                return queue.size();
            } finally {
                lock.unlock();
            }
        }
    }
}
