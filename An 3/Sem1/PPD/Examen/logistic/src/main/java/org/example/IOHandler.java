package org.example;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IOHandler {

    public static void createInputFile(int numberOfCommands) {
        try {
            File directory = new File("src/main/resources/inputs/");
            if (!directory.exists()) {
                boolean dirsCreated = directory.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Nu s-a putut crea directorul: " + directory.getPath());
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory, "input.txt")))) {
                Random rand = new Random();
                for (int i = 1; i <= numberOfCommands; i++) {
                    int idComanda = i;
                    int prioritate = rand.nextInt(2) + 1;
                    bw.write(idComanda + " " + prioritate + "\n");
                }
                bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Eroare la generarea fisierului input.txt", e);
        }
    }


    public synchronized static List<Comanda> read(String filename) {
        List<Comanda> mesaje = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/inputs/" + filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    int idComanda = Integer.parseInt(parts[0]);
                    int prioritate = Integer.parseInt(parts[1]);
                    mesaje.add(new Comanda(idComanda, prioritate));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mesaje;
    }


    public synchronized static void write(String filename, String text) {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean dirsCreated = parent.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Nu s-a putut crea directorul: " + parent.getPath());
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // Append mode
                bw.write(text);
                bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Eroare la scrierea in fisierul " + filename, e);
        }
    }

    public synchronized static void cleanOutputFile() {
        String filename = "src/main/resources/outputs/output.txt";
        try {
            File file = new File(filename);

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean dirsCreated = parent.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Nu s-a putut crea directorul: " + parent.getPath());
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) { // Overwrite mode
            }
        } catch (IOException e) {
            throw new RuntimeException("Eroare la curatarea fisierului output.txt", e);
        }
    }
}
