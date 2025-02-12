package org.example;

public  class MatrixUtils {
    public static void completeazaMargini(int[][] matrice, int n, int m, int padSize) {
        int newRows = matrice.length;
        int newCols = matrice[0].length;

        int dataStartRow = padSize;
        int dataEndRow = dataStartRow + n - 1;
        int dataStartCol = padSize;
        int dataEndCol = dataStartCol + m - 1;

        for (int i = 0; i < dataStartRow; i++) {
            for (int j = dataStartCol; j <= dataEndCol; j++) {
                matrice[i][j] = matrice[dataStartRow][j];
                matrice[newRows - i - 1][j] = matrice[dataEndRow][j];
            }
        }


        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < dataStartCol; j++) {
                matrice[i][j] = matrice[i][dataStartCol];
                matrice[i][newCols - j - 1] = matrice[i][dataEndCol];
            }
        }

        for (int i = 0; i < dataStartRow; i++) {
            for (int j = 0; j < dataStartCol; j++) {
                matrice[i][j] = matrice[dataStartRow][dataStartCol];
                matrice[i][newCols - j - 1] = matrice[dataStartRow][dataEndCol];
                matrice[newRows - i - 1][j] = matrice[dataEndRow][dataStartCol];
                matrice[newRows - i - 1][newCols - j - 1] = matrice[dataEndRow][dataEndCol];
            }
        }
    }

    public static void afiseazaMatrice(int[][] matrice) {
        for (int[] row : matrice) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}
