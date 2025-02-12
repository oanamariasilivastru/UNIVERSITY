#include <iostream>
#include <fstream>
#include <algorithm>
#include <cstdlib>
#include <chrono>

using namespace std;
using namespace std::chrono;


int* readFromFile(const string& filename, int& numDigits) {
    ifstream file(filename);
    if (!file.is_open()) {
        cerr << "Eroare la deschiderea fisierului " << filename << endl;
        exit(1);
    }

    file >> numDigits;
    if (numDigits <= 0) {
        cerr << "Numarul de cifre trebuie sa fie pozitiv în fisierul " << filename << endl;
        exit(1);
    }

    int* number = new int[numDigits];
    for (int i = numDigits - 1; i >= 0; --i) {
        if (!(file >> number[i])) {
            cerr << "Eroare la citirea cifrei " << i + 1 << " din " << filename << endl;
            delete[] number;
            exit(1);
        }
        if (number[i] < 0 || number[i] > 9) {
            cerr << "Cifra " << number[i] << " din " << filename << " nu este valida (trebuie sa fie între 0 si 9)" << endl;
            delete[] number;
            exit(1);
        }
    }
    file.close();


    return number;
}

int* addLargeNumbers(const int* num1, int size1, const int* num2, int size2, int& resultSize) {
    int maxSize = max(size1, size2);
    resultSize = maxSize + 1;
    int* result = new int[resultSize];
    fill(result, result + resultSize, 0);

    int carry = 0;
    for (int i = 0; i < maxSize; i++) {
        int digit1 = (i < size1) ? num1[i] : 0;
        int digit2 = (i < size2) ? num2[i] : 0;
        int sum = digit1 + digit2 + carry;
        result[i] = sum % 10;
        carry = sum / 10;
    }

    result[maxSize] = carry;

    if (carry == 0) {
        --resultSize;
    }

    return result;
}


void writeToFile(const string& filename, const int* number, int size) {
    ofstream file(filename);
    if (!file.is_open()) {
        cerr << "Eroare la deschiderea fisierului " << filename << " pentru scriere!" << endl;
        exit(1);
    }


    file << size << endl;

    for (int i = size - 1; i >= 0; --i) {
        file << number[i];
    }

    file.close();
}


void printNumber(const int* number, int size) {
    for (int i = size - 1; i >= 0; --i) {
        cout << number[i];
    }
    cout << endl;
}

int main() {
    int size1, size2;

    auto startTime = high_resolution_clock::now();
    int* num1 = readFromFile("Numar1.txt", size1);
    int* num2 = readFromFile("Numar2.txt", size2);


    int resultSize;
    int* result = addLargeNumbers(num1, size1, num2, size2, resultSize);

    cout << "Suma celor doua numere mari este: ";
    printNumber(result, resultSize);

    writeToFile("Numar3.txt", result, resultSize);

    delete[] num1;
    delete[] num2;
    delete[] result;


    auto endTime = high_resolution_clock::now();

    cout << duration<double, milli>(endTime - startTime).count() << endl;

    return 0;
}
