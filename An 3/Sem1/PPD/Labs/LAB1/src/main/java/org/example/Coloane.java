package org.example;

public class Coloane {
    int n, m, p;
    int[][] mat;
    int[][] convMat;

    public Coloane(int n, int m, int p, int[][] mat, int[][] convMat) {
        this.n = n;
        this.m = m;
        this.p = p;
        this.mat = mat;
        this.convMat = convMat;
    }

    int[][] output;

    public int[][] run() throws InterruptedException {
        output = new int[n][m];
        Thread[] threads = new Thread[p];

        int startCol = 0;
        int endCol = -1;
        int colsPerThread = m / p;
        int remainingCols = m % p;

        for(int t = 0; t < p; ++t) {
            endCol = startCol + colsPerThread;
            if (remainingCols > 0) {
                endCol++;
                remainingCols--;
            }

            threads[t] = new MyThread(startCol, endCol);
            threads[t].start();
            startCol = endCol;
        }

        for(int t = 0; t < p; ++t) {
            threads[t].join();
        }

        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < m; ++j)
                System.out.print(output[i][j] + " ");
            System.out.println();
        }
        return output;
    }

    class MyThread extends Thread {
        final private int start, end;

        public MyThread(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run() {
            int k = convMat.length;
            int padSize = k / 2;
            int dataStartRow = padSize;
            int dataStartCol = padSize;

            for (int j = start; j < end; ++j) {
                for (int i = 0; i < n; ++i) {
                    int sum = 0;
                    for (int ki = 0; ki < k; ki++) {
                        for (int kj = 0; kj < k; kj++) {
                            int rowIndex = dataStartRow + i + ki - padSize;
                            int colIndex = dataStartCol + j + kj - padSize;
                            if (rowIndex >= 0 && rowIndex < mat.length &&
                                    colIndex >= 0 && colIndex < mat[0].length) {
                                sum += mat[rowIndex][colIndex] * convMat[ki][kj];
                            }
                        }
                    }
                    output[i][j] = sum;
                }
            }

        }
    }
}
