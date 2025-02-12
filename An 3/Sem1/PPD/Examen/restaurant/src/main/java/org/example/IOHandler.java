package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOHandler {
    private static final String LOG_FILE = "fisier.log";

    public synchronized static void writeText(String text) {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            fileWriter.write(text);
            fileWriter.newLine();
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Eroare la scrierea în fișierul log: " + e.getMessage());
        }
    }


    public synchronized static void clearLogFile() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(LOG_FILE))) {
            fileWriter.write("");
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Eroare la golirea fișierului log: " + e.getMessage());
        }
    }
}
