package org.example;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOHandler {
    private static final String LOG_FILE = "clinic_log.txt";

    public synchronized static void writeText(String text) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Eroare la scrierea în fișierul log: " + e.getMessage());
        }
    }

    public synchronized static void clearLogFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE))) {
            bw.write("");
        } catch (IOException e) {
            throw new RuntimeException("Eroare la golirea fișierului log: " + e.getMessage());
        }
    }
}