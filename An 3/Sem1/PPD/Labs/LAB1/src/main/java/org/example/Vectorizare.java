package org.example;

public class Vectorizare {
    int n, m, p;
    int[][] mat;
    int[][] convMat;

    public Vectorizare(int n, int m, int p, int[][] mat, int[][] convMat) {
        this.n = n;
        this.m = m;
        this.p = p;
        this.mat = mat;
        this.convMat = convMat;
    }

    int[][] newMat;

    public int[][] run() throws InterruptedException {
        newMat = new int[n][m];
        Thread[] threads = new Thread[p];

        int totalElements = n * m;
        int rowsPerThread = totalElements / p;

        for (int k = 0; k < p; ++k) {
            int startRow = k * rowsPerThread;
            int endRow = (k == p - 1) ? totalElements : (k + 1) * rowsPerThread;

            threads[k] = new MyThread(startRow, endRow);
            threads[k].start();
        }

        for (int i = 0; i < p; ++i)
            threads[i].join();

        for (int i = 0; i < n; ++i) {
            System.out.println();
            for (int j = 0; j < m; ++j)
                System.out.print(newMat[i][j] + " ");
        }
        return newMat;
    }

    class MyThread extends Thread {
        final private int startRow, endRow;

        public MyThread(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        public void run() {
            int k = convMat.length;
            int padSize = k / 2;
            for (int i = startRow; i < endRow; ++i) {
                for (int j = 0; j < m; ++j) {
                    int sum = 0;
                    for (int ki = 0; ki < k; ki++) {
                        for (int kj = 0; kj < k; kj++) {
                            int rowIndex = i + ki - padSize;
                            int colIndex = j + kj - padSize;
                            if (rowIndex >= 0 && colIndex >= 0 && rowIndex < n && colIndex < m) {
                                sum += mat[rowIndex][colIndex] * convMat[ki][kj];
                            }
                        }
                    }
                    newMat[i][j] = sum;
                }
            }
        }
    }
}
