package org.example;

public class Blocuri {
    int n, m, p;
    int[][] mat;
    int[][] convMat;

    public Blocuri(int n, int m, int p, int[][] mat, int[][] convMat) {
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

        int totalElements = n * m;
        int elementsPerThread = totalElements / p;
        int remainingElements = totalElements % p;

        int startIdx = 0;
        for (int t = 0; t < p; ++t) {
            int endIdx = startIdx + elementsPerThread;
            if (remainingElements > 0) {
                endIdx++;
                remainingElements--;
            }

            threads[t] = new MyThread(startIdx, endIdx);
            threads[t].start();
            startIdx = endIdx;
        }

        for (int t = 0; t < p; ++t) {
            threads[t].join();
        }

        // Print the output matrix
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j)
                System.out.print(output[i][j] + " ");
            System.out.println();
        }

        return output;
    }

    class MyThread extends Thread {
        private final int startIdx, endIdx;

        public MyThread(int startIdx, int endIdx) {
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        public void run() {
            int k = convMat.length;
            int padSize = k / 2;
            int dataStartRow = padSize;
            int dataStartCol = padSize;

            for (int idx = startIdx; idx < endIdx; ++idx) {
                int i = idx / m;
                int j = idx % m;

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
