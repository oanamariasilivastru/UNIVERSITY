package org.example;

public class Linii {
    int n, m, p;
    int[][] mat;
    int[][] convMat;

    public Linii(int n, int m, int p, int[][] mat, int[][] convMat){
        this.n = n;
        this.m = m;
        this.p = p;
        this.mat = mat;
        this.convMat = convMat;
    }

    int[][] output;

    public int[][] run() throws InterruptedException{
        output = new int[n][m];
        Thread[] threads = new Thread[p];

        int startRow = 0;
        int endRow = -1;
        int rowsPerThread = n / p;
        int remainingRows = n % p;

        for(int t = 0; t < p; ++t){
            endRow = startRow + rowsPerThread;
            if(remainingRows > 0){
                endRow++;
                remainingRows--;
            }
            threads[t] = new MyThread(startRow, endRow);
            threads[t].start();
            startRow = endRow;
        }

        for(int t=0; t < p ;++t){
            try{
                threads[t].join();
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        // Print the output matrix
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

            for(int i = start; i < end; ++i) {
                for (int j = 0; j < m; ++j) {
                    int sum = 0;
                    for (int ki = 0; ki < k; ki++) {
                        for (int kj = 0; kj < k; kj++) {
                            int rowIndex = dataStartRow + i + ki - padSize;
                            int colIndex = dataStartCol + j + kj - padSize;
                            sum += mat[rowIndex][colIndex] * convMat[ki][kj];
                        }
                    }
                    output[i][j] = sum;
                }
            }
        }
    }
}
