package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    static int UPPER_BOUND = 10;

    public static void main(String[] args) {
        int n = 0, m = 0, k = 0;
        String numeFisier = "date.txt";
        int[][] matrice = null;
        int[][] kernel = null;
        int[][] result = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(numeFisier));
            String line = br.readLine();
            String[] dimensiuni = line.trim().split("\\s+");
            n = Integer.parseInt(dimensiuni[0]);
            m = Integer.parseInt(dimensiuni[1]);
            k = Integer.parseInt(dimensiuni[2]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        genereazaSiScrie(n, m, k, numeFisier);
        Data data = citesteMatriceDinFisier(numeFisier, n, m, k);
        matrice = data.matrice;
        kernel = data.kernel;

        int padSize = k / 2;

        int[][] matriceExtinsa = new int[n + 2 * padSize][m + 2 * padSize];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matriceExtinsa[i + padSize][j + padSize] = matrice[i][j];
            }
        }

        MatrixUtils.completeazaMargini(matriceExtinsa, n, m, padSize);
        System.out.println("Matricea bordata: ");
        MatrixUtils.afiseazaMatrice(matriceExtinsa);

        long starTime = System.currentTimeMillis();

//        Secvential s1 = new Secvential(n, m, k, matriceExtinsa, kernel);
//        result1 = s1.run();
        int p = 4;
//        p = Integer.parseInt(args[0]);
//        Linii l1 = new Linii(n, m, p, matriceExtinsa, kernel);
        Coloane c1 = new Coloane(n, m, p, matriceExtinsa, kernel);
        Vectorizare v1 = new Vectorizare(n, m, p, matriceExtinsa, kernel);

        try {
            //result = l1.run();
            result = c1.run();
            //result = v1.run();
        } catch (InterruptedException ex){
            throw new RuntimeException(ex);
        }

        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println((double)(endTime - starTime) / 1E6);
//        scrieMatriceInFisier(result, "matrice.txt");
        scrieMatriceInFisier(result, "out.txt");

        boolean esteCorect = verificaCorectitudinea("referinta.txt", "out.txt");
        if (esteCorect) {
            System.out.println("Rezultatul este corect.");
        } else {
            System.out.println("Rezultatul nu este corect.");
        }
    }

    public static void genereazaSiScrie(int n, int m, int k, String numeFisier){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(numeFisier));

            bw.write(n + " " + m + " " + k);
            bw.newLine();

            Random rand = new Random();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    bw.write(rand.nextInt(UPPER_BOUND) + " ");
                }
                bw.newLine();
            }

            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    bw.write(rand.nextInt(UPPER_BOUND) + " ");
                }
                bw.newLine();
            }
            bw.close();
            System.out.println("\nMatricile au fost generate si scrise în fisierul " + numeFisier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Data citesteMatriceDinFisier(String numeFisier, int n, int m, int k) {
        int[][] matrice = new int[n][m];
        int[][] kernel = new int[k][k];

        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            br.readLine();
            for (int i = 0; i < n; i++) {
                String[] valori = br.readLine().trim().split("\\s+");
                for (int j = 0; j < m; j++) {
                    matrice[i][j] = Integer.parseInt(valori[j]);
                }
            }

            for (int i = 0; i < k; i++) {
                String[] valori = br.readLine().trim().split("\\s+");
                for (int j = 0; j < k; j++) {
                    kernel[i][j] = Integer.parseInt(valori[j]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Data(matrice, kernel);
    }

    public static void scrieMatriceInFisier(int[][] matrice, String numeFisier) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(numeFisier))) {
            for (int[] row : matrice) {
                for (int value : row) {
                    bw.write(value + " ");
                }
                bw.newLine();
            }
            System.out.println("Rezultatul a fost scris în fișierul " + numeFisier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verificaCorectitudinea(String fisierReferinta, String fisierTest) {
        Path pathReferinta = Paths.get(fisierReferinta);
        Path pathTest = Paths.get(fisierTest);

        try {
            byte[] bytesReferinta = Files.readAllBytes(pathReferinta);
            byte[] bytesTest = Files.readAllBytes(pathTest);
            return Arrays.equals(bytesReferinta, bytesTest);
        } catch (IOException e) {
            System.out.println("Eroare la citirea fișierelor: " + e.getMessage());
            return false;
        }
    }
}
