package org.example;

public class Secvential {
    int n, m, k;
    int[][] mat;
    int[][] convMat;

    public Secvential(int n, int m, int k, int[][] mat, int[][] convMat){
        this.n = n;
        this.m = m;
        this.k = k;
        this.mat = mat;
        this.convMat = convMat;
    }

    public void run(){
        int padSize = k / 2;
        int[] prevRow = new int[m];
        int[] currRow = new int[m];
        int[] nextRow = new int[m];

        System.arraycopy(mat[0], 0, prevRow, 0, m);
        if(n > 1){
            System.arraycopy(mat[1], 0, currRow, 0, m);
        }
        else{
            System.arraycopy(mat[0], 0, currRow, 0, m);
        }

        for(int i = 0; i < n; i++){
            int nextRowIdx = i + padSize + 1;
            if(nextRowIdx < n){
                System.arraycopy(mat[nextRowIdx], 0, nextRow, 0, m);
            }
            else{
                System.arraycopy(mat[n - 1], 0, nextRow, 0, m);
            }

            for(int j = 0; j < m; j++){
                int sum = 0;
                for(int ki = 0; ki < k; ki++)
                    for(int kj = 0; kj < k; kj++){
                        int neighbor_i = i + ki - padSize;
                        int neighbor_j = j + kj - padSize;
                        int pixel;
                        if(neighbor_i < 0){
                            pixel = prevRow[clamp(neighbor_j, 0, m - 1)];
                        }
                        else if(neighbor_i >= n){
                            pixel = nextRow[clamp(neighbor_j, 0, m - 1)];
                        }
                        else{
                            if(neighbor_i < i){
                                pixel = prevRow[clamp(neighbor_j, 0, m - 1)];
                            }
                            else if(neighbor_i > i){
                                pixel = nextRow[clamp(neighbor_j, 0, m - 1)];
                            }
                            else{
                                pixel = mat[i][clamp(neighbor_j, 0, m - 1)];
                            }
                        }
                        sum += pixel * convMat[ki][kj];
                    }
                mat[i][j] = sum;
            }

            int[] temp = prevRow;
            prevRow = currRow;
            currRow = nextRow;
            nextRow = temp;
        }
    }

    private int clamp(int val, int min, int max){
        if(val < min) return min;
        if(val > max) return max;
        return val;
    }
}
