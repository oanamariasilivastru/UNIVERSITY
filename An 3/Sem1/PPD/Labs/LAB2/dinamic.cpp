#include <iostream>
#include <thread>
#include <chrono>
#include <fstream>
using namespace std;

int n, m, k;
const int p = 4;
int** mat, ** convMat;
int** newMat;

void readInput() {
    ifstream inputFile("date.txt");
    if (!inputFile) {
        cerr << "Error opening file." << endl;
        return;
    }

    inputFile >> n >> m >> k;
    mat = new int* [n];
    newMat = new int* [n];

    for (int i = 0; i < n; ++i) {
        mat[i] = new int[m];
        newMat[i] = new int[m];
        for (int j = 0; j < m; ++j) {
            inputFile >> mat[i][j];
        }
    }

    convMat = new int* [k];

    for (int i = 0; i < k; ++i) {
        convMat[i] = new int[k];
        for (int j = 0; j < k; ++j) {
            inputFile >> convMat[i][j];
        }
    }

    inputFile.close();
}

void secvential() {
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < m; ++j) {
            int sum = 0;
            for (int i1 = 0; i1 < k; ++i1) {
                for (int j1 = 0; j1 < k; ++j1) {
                    if (i - k / 2 + i1 >= 0 && j - k / 2 + j1 >= 0 && i - k / 2 + i1 < n && j - k / 2 + j1 < m) {
                        sum += mat[i - k / 2 + i1][j - k / 2 + j1] * convMat[i1][j1];
                    }
                }
            }
            newMat[i][j] = sum;
        }
    }

    ofstream outFile("output.txt");

    if (outFile.is_open()) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                outFile << newMat[i][j] << " ";
            }
            outFile << endl;
        }
        outFile.close();
    }
    else {
        cout << "Nu s-a putut deschide fisierul." << endl;
    }
}

class ThreadLinii {
private:
    int start, stop;

public:
    ThreadLinii(int start, int stop) : start(start), stop(stop) {}

    void operator()() {
        for (int i = start; i < stop; ++i) {
            for (int j = 0; j < m; ++j) {
                int sum = 0;
                for (int i1 = 0; i1 < k; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        if (i - k / 2 + i1 >= 0 && j - k / 2 + j1 >= 0 &&
                            i - k / 2 + i1 < n && j - k / 2 + j1 < m) {
                            sum += mat[i - k / 2 + i1][j - k / 2 + j1] * convMat[i1][j1];
                        }
                    }
                }
                newMat[i][j] = sum;
            }
        }
    }
};

void linii() {
    int startRow = 0;
    int endRow = -1;
    int rowsPerThread = n / p;
    int remainingRows = n % p;

    thread threads[p];

    for (int k = 0; k < p; ++k) {
        endRow = startRow + rowsPerThread;
        if (remainingRows > 0) {
            endRow++;
            remainingRows--;
        }

        threads[k] = thread(ThreadLinii(startRow, endRow));
        startRow = endRow;
    }

    for (int i = 0; i < p; ++i)
        threads[i].join();

    ofstream outFile("output.txt");

    if (outFile.is_open()) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                outFile << newMat[i][j] << " ";
            }
            outFile << endl;
        }
        outFile.close();
    }
    else {
        cout << "Nu s-a putut deschide fisierul." << endl;
    }
}

class ThreadColoane {
private:
    int start, stop;

public:
    ThreadColoane(int start, int stop) : start(start), stop(stop) {}

    void operator()() {
        for (int j = start; j < stop; ++j) {
            for (int i = 0; i < n; ++i) {
                int sum = 0;
                for (int i1 = 0; i1 < k; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        if (i - k / 2 + i1 >= 0 && j - k / 2 + j1 >= 0 &&
                            i - k / 2 + i1 < n && j - k / 2 + j1 < m) {
                            sum += mat[i - k / 2 + i1][j - k / 2 + j1] * convMat[i1][j1];
                        }
                    }
                }
                newMat[i][j] = sum;
            }
        }
    }
};

void coloane() {
    int startCol = 0;
    int endCol = -1;
    int colsPerThread = m / p;
    int remainingCols = m % p;

    thread threads[p];

    for (int k = 0; k < p; ++k) {
        endCol = startCol + colsPerThread;
        if (remainingCols > 0) {
            endCol++;
            remainingCols--;
        }

        threads[k] = thread(ThreadColoane(startCol, endCol));
        startCol = endCol;
    }

    for (int i = 0; i < p; ++i)
        threads[i].join();

    ofstream outFile("output.txt");

    if (outFile.is_open()) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                outFile << newMat[i][j] << " ";
            }
            outFile << endl;
        }
        outFile.close();
    }
    else {
        cout << "Nu s-a putut deschide fisierul." << endl;
    }
}

class ThreadVectorizare {
private:
    int start, stop;

public:
    ThreadVectorizare(int start, int stop) : start(start), stop(stop) {}

    void operator()() {
        for (int index = start; index < stop; index++) {
            int i = index / m;
            int j = index % m;
            int sum = 0;
            for (int i1 = 0; i1 < k; ++i1) {
                for (int j1 = 0; j1 < k; ++j1) {
                    if (i - k / 2 + i1 >= 0 && j - k / 2 + j1 >= 0 &&
                        i - k / 2 + i1 < n && j - k / 2 + j1 < m) {
                        sum += mat[i - k / 2 + i1][j - k / 2 + j1] * convMat[i1][j1];
                    }
                }
            }
            newMat[i][j] = sum;
        }
    }
};

void vectorizare() {
    int totalElements = n * m;
    int elementsPerThread = totalElements / p;
    int rest = totalElements % p;
    int restCompleted = 0;

    thread threads[p];

    for (int k = 0; k < p; ++k) {
        int startIndex = k * elementsPerThread + restCompleted;
        int endIndex = (k == p - 1) ? totalElements : (k + 1) * elementsPerThread;

        if (rest != 0) {
            --rest;
            ++endIndex;
            ++restCompleted;
        }

        threads[k] = thread(ThreadVectorizare(startIndex, endIndex));
    }

    for (int i = 0; i < p; ++i)
        threads[i].join();

    ofstream outFile("output.txt");

    if (outFile.is_open()) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                outFile << newMat[i][j] << " ";
            }
            outFile << endl;
        }
        outFile.close();
    }
    else {
        cout << "Nu s-a putut deschide fisierul." << endl;
    }
}

int main() {
    auto start = chrono::high_resolution_clock::now();

    readInput();
    //secvential();
    //linii();
    //coloane();
    vectorizare();

    auto stop = chrono::high_resolution_clock::now();
    chrono::duration<double, std::micro> duration = stop - start;
    double microseconds = duration.count();
    cout << microseconds / 1000000 << endl;
    return 0;
}
