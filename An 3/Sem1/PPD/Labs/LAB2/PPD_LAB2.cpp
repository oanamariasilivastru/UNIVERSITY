#include <iostream>
#include <thread>
#include <chrono>
#include <fstream>
#include <vector>
using namespace std;

const int MAX_N = 100;
const int MAX_M = 100;

int n, m, k;
int p;
int mat[MAX_N][MAX_M];
int convMat[MAX_N][MAX_N];
int newMat[MAX_N][MAX_M];

void readInput() {
    ifstream inputFile("date.txt");
    if (!inputFile) {
        cerr << "Error opening file." << endl;
        return;
    }

    inputFile >> n >> m >> k;

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < m; ++j) {
            inputFile >> mat[i][j];
        }
    }

    for (int i = 0; i < k; ++i) {
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
                    int row = i - k / 2 + i1;
                    int col = j - k / 2 + j1;
                    if (row >= 0 && col >= 0 && row < n && col < m) {
                        sum += mat[row][col] * convMat[i1][j1];
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
                        int row = i - k / 2 + i1;
                        int col = j - k / 2 + j1;
                        if (row >= 0 && col >= 0 && row < n && col < m) {
                            sum += mat[row][col] * convMat[i1][j1];
                        }
                    }
                }
                newMat[i][j] = sum;
            }
        }
    }
};

void linii() {
    int rowsPerThread = n / p;
    int remainingRows = n % p;

    vector<thread> threads;
    int startRow = 0;

    for (int k = 0; k < p; ++k) {
        int endRow = startRow + rowsPerThread;
        if (remainingRows > 0) {
            endRow++;
            remainingRows--;
        }

        threads.emplace_back(ThreadLinii(startRow, endRow));
        startRow = endRow;
    }

    for (auto& th : threads)
        th.join();

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
                        int row = i - k / 2 + i1;
                        int col = j - k / 2 + j1;
                        if (row >= 0 && col >= 0 && row < n && col < m) {
                            sum += mat[row][col] * convMat[i1][j1];
                        }
                    }
                }
                newMat[i][j] = sum;
            }
        }
    }
};

void coloane() {
    int colsPerThread = m / p;
    int remainingCols = m % p;

    vector<thread> threads;
    int startCol = 0;

    for (int k = 0; k < p; ++k) {
        int endCol = startCol + colsPerThread;
        if (remainingCols > 0) {
            endCol++;
            remainingCols--;
        }

        threads.emplace_back(ThreadColoane(startCol, endCol));
        startCol = endCol;
    }

    for (auto& th : threads)
        th.join();

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
                    int row = i - k / 2 + i1;
                    int col = j - k / 2 + j1;
                    if (row >= 0 && col >= 0 && row < n && col < m) {
                        sum += mat[row][col] * convMat[i1][j1];
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
    int remainingElements = totalElements % p;

    vector<thread> threads;
    int startIndex = 0;

    for (int k = 0; k < p; ++k) {
        int endIndex = startIndex + elementsPerThread;
        if (remainingElements > 0) {
            endIndex++;
            remainingElements--;
        }

        threads.emplace_back(ThreadVectorizare(startIndex, endIndex));
        startIndex = endIndex;
    }

    for (auto& th : threads)
        th.join();

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

int main(int argc, char* argv[]) {
    if (argc < 2) {
        cerr << "Usage: " << argv[0] << " <number of threads>" << endl;
        return 1;
    }

    p = atoi(argv[1]);

    auto start = chrono::high_resolution_clock::now();

    readInput();
    // secvential();
    // linii();
    // coloane();
    // blocuri();
    // vectorizare();

    auto stop = chrono::high_resolution_clock::now();
    chrono::duration<double, std::micro> duration = stop - start;
    double microseconds = duration.count();
    cout << microseconds / 1000000 << endl;
    return 0;
}
